package com.batchapplication.springbatchapplication.part3;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class itemWriterConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;


    @Bean
    public Job itemWriterJob() throws Exception {
        return jobBuilderFactory.get("itemWriterJob")
                .incrementer(new RunIdIncrementer())
                .start(this.csvItemWriterStep())
//                .next(this.jdbcBatchItemWriterStep())
                .next(this.jpaItemWriterStep())
                .build();
    }

    @Bean
    public Step csvItemWriterStep() throws Exception {
        return stepBuilderFactory.get("csvItemWriterStep")
                .<Person, Person>chunk(10)
                .reader(itemReader())
                .writer(csvFileItemWriter())
                .build();
    }

    //reader -> processing - > writer

    @Bean
    public Step jdbcBatchItemWriterStep() {
        return stepBuilderFactory.get("jdbcBatchItemWriterStep")
                .<Person, Person>chunk(10)
                .reader(itemReader())
                .writer(jdbcBatchFileItemWriter())
                .build();
    }

    @Bean
    public Step jpaItemWriterStep() throws Exception {
        return stepBuilderFactory.get("jpaItemWriterStep")
                .<Person, Person>chunk(10)
                .reader(itemReader())
                .writer(jpaItemWriter())
                .build();
    }

    private ItemWriter<Person> jpaItemWriter() throws Exception {
        JpaItemWriter<Person> itemWriter = new JpaItemWriterBuilder<Person>()
                .entityManagerFactory(entityManagerFactory)
                .usePersist(true)
                .build();
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }

    private ItemWriter<Person> jdbcBatchFileItemWriter() {

        JdbcBatchItemWriter<Person> jdbcBatchItemWriter = new JdbcBatchItemWriterBuilder<Person>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("insert into person(name ,age, address) values(:name, :age, :address)")
                .build();
        jdbcBatchItemWriter.afterPropertiesSet();
        return jdbcBatchItemWriter;
    }

    private ItemWriter<Person> csvFileItemWriter() throws Exception {
        //csv 파일의 데이터를 추추하기위해서 fieldExtractor 가 필요함
        BeanWrapperFieldExtractor<Person> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"id", "name", "age", "address"});

        //각필드 라인에 데이터를 넣기위해서
        DelimitedLineAggregator<Person> delimitedLineAggregator = new DelimitedLineAggregator<>();

        delimitedLineAggregator.setDelimiter(",");
        delimitedLineAggregator.setFieldExtractor(fieldExtractor);
        //


        FlatFileItemWriter<Person> itemWriter = new FlatFileItemWriterBuilder<Person>()
                .name("csvFileItemWriter")
                .encoding("UTF-8")
                .resource(new FileSystemResource("output/test-output.csv"))
                .lineAggregator(delimitedLineAggregator)
                .headerCallback(writer -> writer.write("id,이름,나이,거주지"))
                .footerCallback(writer -> writer.write("---------------\n"))
                .append(true)
                .build();
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }

    private ItemReader<Person> itemReader() {
        return new CustomItemReader<>(getItems());
    }

    private List<Person> getItems() {
        List<Person> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            items.add(new Person("testname", "testage" + i, "test address"));
        }
        return items;
    }
}
