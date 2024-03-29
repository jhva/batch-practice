//package com.batchapplication.springbatchapplication.part3;
//
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@Slf4j
//@RequiredArgsConstructor
//public class SpringBatch {
//
//    private final JobBuilderFactory jobBuilderFactory;
//    private final StepBuilderFactory stepBuilderFactory;
//
//
//    @Bean
//    public Job itemReaderJob() {
//        return jobBuilderFactory.get("itemReaderJob")
//                .incrementer(new RunIdIncrementer())
//                .start(this.customItemReaderStep())
//                .build();
//    }
//
//    @Bean
//    public Step customItemReaderStep() {
//        return stepBuilderFactory.get("customItemReaderStep")
//                .chunk()
//                .reader()
//                .processor()
//                .writer()
//                .build();
//    }
//}
