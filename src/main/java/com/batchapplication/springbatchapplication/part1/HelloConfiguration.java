package com.batchapplication.springbatchapplication.part1;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class HelloConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    public HelloConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job helloJob() {
        return jobBuilderFactory.get("helloJob")
                .incrementer(new RunIdIncrementer())//항상 job이실행될때마다 id를 생성
                .start(this.helloStep())
                .build();
    }

    @Bean
    //한개이상의 step을만들수이싿 .step은 tasklet으로 처리할수있다 .
    public Step helloStep() {
        return stepBuilderFactory.get("helloStep")
                .tasklet(((contribution, chunkContext) -> {
                    log.info("hello spring batch");
                    return RepeatStatus.FINISHED;
                })).build();
    }
}
/**
 * 스프링 배치에서 job은 실행 단위라고 생각해야한다
 * <p>
 * job을만들기위해서 jobBuilderFactory 로
 */


/**
 * 하나의 job은 같은 파라미터로 재실행할수없다 .
 * excutionContext라는 객체는  job 과 step에 context를 관리하는 객체  이를 통해 공유할수있다 ,.
 */