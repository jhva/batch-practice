* 스프링 배치에서 job은 실행 단위라고 생각해야한다
* <p>
* job을만들기위해서 jobBuilderFactory 로


* 하나의 job은 같은 파라미터로 재실행할수없다 .
* excutionContext라는 객체는 job 과 step에 context를 관리하는 객체 이를 통해 공유할수있다 ,.

job의 excutionContext step excutionContext 의 사용범위

job이관리하는 step내에서 어디서든 공유

step은 해당step에서만 가능

배치를 처리할 수 있는 방법은 크게 2가지

# Tasklet을 사용한 task기반처리

- 배치 처릭 과정이 비교적 쉬운 경우 쉽게사용
- 대량 처리를 하는 경우 더 복잡
- 하나의 큰 덩어리를 여러 덩어리로 나누어 처리하기 부적합 

## Chunk를 사용한 chunk(덩어리) 기반 처리 
- itemReader,itemProcessor, itemWriter의 관계 이해 필요 
- 대량 처리를 하는 경우 Tasklet보다 비교적 쉽게 구혖ㄴ
- 예를 들어서 10000개의 데이터 중 1000개씩 10개 덩어리로 수행 
- 이를 Tasklet으로 처리하면 10000개를 한번에 처리하거나 수ㅡ동으로 1000개씩 분할 
- 

# Scope 
jobScope && stepScope 
- Spring에서 Scope는 @Bean의 라이프사이클 (싱글톤)


# Cursor

- 배치처리가 완료될때ㅔ 까지 DB Connection 연결 
- DB Connection  빈도가 낮아 성능이 좋은 반며느 긴 connection 유지 필요 
- 하나의 connection 에서 처리되기때문에 Thread Safe 하지 않음 
- 모드 ㄴ결과를 메모리에 할당하기때문에 , 더 많은 메모리를 사용 

# Paging 
- 페이징 단위로 DB Connection 연결 
- DB connection 빈도가 높아 비교적 성능이 낮은 반면,짧은 Connection유지 시간 필요 
- 매번 Connection 을 하기 때문에 Thread Safe
- 페이징 단위의 결과만 메모리에 할당하기 때문에 , 비교적 더 적은 메모리르 ㄹ 사용 
