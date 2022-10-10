package com.batchapplication.springbatchapplication.part4;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "Users")
@Getter
@NoArgsConstructor // 기본 생성자
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;


    @Enumerated(EnumType.STRING)
    private Level level = Level.NORMAL;

    private int totalAmount; //총 주문금액

    private LocalDate updatedDate;

    //생성자 기준으로 빌더 객체가 제공
    @Builder
    private User(String username, int totalAmount) {
        this.username = username;
        this.totalAmount = totalAmount;
    }

    public boolean availableLevelUp() {
        return Level.availableLevelUp(this.getLevel(), this.getTotalAmount());
    }

    public Level levelUp() {
        Level nextLevel = Level.getNextLevel(this.getTotalAmount());
        this.level = nextLevel;
        this.updatedDate = LocalDate.now();

        return nextLevel;

    }

    public enum Level {
        VIP(500_000, null),
        GOLD(500_00, VIP),
        SILVER(300_000, GOLD),
        NORMAL(200_000, SILVER);

        private final int nextAmount;

        private final Level nextLevel;


        Level(int nextAmoout, Level nextLevel) {
            this.nextAmount = nextAmoout;
            this.nextLevel = nextLevel;
        }

        private static boolean availableLevelUp(Level level, int totalAmount) {
            if (Objects.isNull(level)) {
                return false;
            }
            if (Objects.isNull(level.nextLevel)) {
                return false;
            }
            return totalAmount >= level.nextAmount;
        }


        private static Level getNextLevel(int totalAmount) {
            if (totalAmount >= Level.VIP.nextAmount) {
                return VIP;

            }
            if (totalAmount >= Level.GOLD.nextAmount) {
                return GOLD.nextLevel;

            }
            if (totalAmount >= Level.SILVER.nextAmount) {
                return SILVER.nextLevel;

            }
            if (totalAmount >= Level.NORMAL.nextAmount) {
                return NORMAL.nextLevel;

            }
            return NORMAL;
        }
    }
}

