package com.asyncexcel.springboot;

import com.asyncexcel.core.exporter.AsyncExportTaskSupport;
import com.asyncexcel.core.exporter.ExportSupport;
import com.asyncexcel.core.importer.AsyncImportTaskSupport;
import com.asyncexcel.core.importer.ImportSupport;
import com.asyncexcel.core.service.IStorageService;
import com.asyncexcel.core.service.TaskService;
import com.asyncexcel.springboot.context.service.ServerLocalStorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Description 注册给子容器的bean通过ComponentScan 与 自定义@ExcelHandle注解扫描写在主项目中的扩展bean
 * @Author 姚仲杰#80998699
 * @Date 2022/7/6 15:18
 */
@Configuration
@ComponentScan({"com.asyncexcel.springboot.context"})
@Import(ExcelContextRegistrar.class)
public class ExcelContextConfiguration{
    
    
    /**暴露给外部扩展
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(IStorageService.class)
    IStorageService storageService(){
        return new ServerLocalStorageService();
    }
    
    
    @Bean
    @ConditionalOnBean({IStorageService.class, TaskService.class})
    ImportSupport importSupport(TaskService taskService,IStorageService storageService){
        return new AsyncImportTaskSupport(storageService,taskService);
    }
    
    
    @Bean
    @ConditionalOnBean({IStorageService.class,TaskService.class})
    ExportSupport exportSupport(TaskService taskService,IStorageService storageService){
        return new AsyncExportTaskSupport(storageService,taskService);
    }
}
