package com.sourav.app.json.config;

import com.sourav.app.csv.model.Student;
import com.sourav.app.json.processor.JsonProcessor;
import com.sourav.app.json.writer.JsonWriter;
import com.sourav.app.listener.CustomSkipListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class JsonJob {
    @Autowired
    private JsonItemReader jsonReader;
    @Autowired
    private JsonWriter jsonWriter;

    @Autowired
    private JsonProcessor jsonProcessor;
    @Autowired
    private CustomSkipListener skipListener;

    private static Logger logger = LoggerFactory.getLogger(JsonJob.class);
    @Bean
    public Job firstJob(JobRepository jobRepository, @Qualifier("firstChunkStep") Step firstChunkStep) {
        return new JobBuilder("First Job", jobRepository)
                .start(firstChunkStep)
                .build();
    }

    @Bean
    public Step firstChunkStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        logger.info("Reading Json....");
        System.out.println("Reading Json....");
        return new StepBuilder("First Step", jobRepository)
                .<Student, Student>chunk(3, transactionManager)
                .reader(jsonReader)
                .processor(jsonProcessor)
                .writer(jsonWriter.jsonFileItemWriter())
                .faultTolerant()
                .skip(Throwable.class)
//                .skip(FlatFileParseException.class)
//                .skip(NullPointerException.class)
                .skipLimit(2)
                //.skipPolicy(new AlwaysSkipItemSkipPolicy()) // Do not use max skip limit and retry together otherwise it will be infinite loop
                .listener(skipListener)
                .retryLimit(1)
                .retry(Throwable.class)
                .build();

//        try{
//
//        }catch (FlatFileParseException ex){
//
//        }
    }
}
