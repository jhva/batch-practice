package com.batchapplication.springbatchapplication.part2;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SharedConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    public SharedConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job sharedJob() {
        return jobBuilderFactory.get("sharedJob")
                .incrementer(new RunIdIncrementer())
                .start(this.sharedStep()) // excutioncontext 의 임의
                .next(this.sharedStep2()) // excutioncontext넣은 값을 로그로 찍어봄
                .build();
    }

    @Bean
    public Step sharedStep2() {
        return stepBuilderFactory.get("shareStep2")
                .tasklet((contribution, chunkContext) -> {
                    // step ExecutionContext.get
                    StepExecution stepExecution = contribution.getStepExecution();
                    ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();

                    // job ExecutionContext.get
                    JobExecution jobExecution = stepExecution.getJobExecution();
                    ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();

                    // log
                    log.info("jobValue : {}, stepValue : {}",
                            jobExecutionContext.getString("job", "emptyJob"),
                            stepExecutionContext.getString("step", "emptyStep"));

                    return RepeatStatus.FINISHED;

                }).build();
    }

    @Bean
    public Step sharedStep() {
        return stepBuilderFactory.get("shareStep1")
                .tasklet((contribution, chunkContext) -> { // contribution 객체가있음
                    // step ExecutionContext.put
                    StepExecution stepExecution = contribution.getStepExecution();
                    ExecutionContext stepExecutionContext = stepExecution.getExecutionContext();
                    stepExecutionContext.putString("step", "step execution context");

                    // job ExecutionContext.put
                    JobExecution jobExecution = stepExecution.getJobExecution();
                    ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
                    jobExecutionContext.putString("job", "job execution context");

                    // log
                    JobInstance jobInstance = jobExecution.getJobInstance();
                    JobParameters jobParameters = jobExecution.getJobParameters();
//                    JobParameters jobParameters1 = stepExecution.getJobParameters();

                    log.info("jobName : {}, stepName : {}, run.id : {}",
                            jobInstance.getJobName(),
                            stepExecution.getStepName(),
                            jobParameters.getLong("run.id"));

                    return RepeatStatus.FINISHED;
                }).build();
    }
}
/**
 * job의 excutionContext step excutionContext 의 사용범위
 *
 * job이관리하는 step내에서 어디서든 공유
 *
 *
 * step은 해당step에서만 가능
 */