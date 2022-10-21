package com.asyncexcel.springboot;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Description 懒加载子容器,直到调用时才会初始化容器，可能会造成错误无法预知，在下一个版本将会改成先加载
 * @Author 姚仲杰#80998699
 * @Date 2022/7/7 16:51
 */
@Configuration
public class ExcelAutoConfiguration {
    
    @Bean
    public SpringExcelContext springExcelContext(){
        SpringExcelContext context = new SpringExcelContext();
        return context;
    }
    
    @Bean
    public ExcelService excelService(SpringExcelContext springExcelContext,ExcelThreadPool excelThreadPool){
        return new ExcelService(excelThreadPool,springExcelContext);
    }
    
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
