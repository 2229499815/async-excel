package com.asyncexcel.springboot;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description 默认线程池实现
 * @Author 姚仲杰#80998699
 * @Date 2022/8/25 10:35
 */
@Configuration
public class DefaultThreadPoolConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public ExcelThreadPool excelThreadPool(){
        int processors = Runtime.getRuntime().availableProcessors();
        int coreSize=2;
        int maxSize=4;
        if (processors>1){
            coreSize=2*processors-1;
            maxSize=4*processors-1;
        }
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            coreSize,
            maxSize,
            0,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(20)
        );
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdownNow();
        }));
        return new ExcelThreadPool(executor);
    }
}
