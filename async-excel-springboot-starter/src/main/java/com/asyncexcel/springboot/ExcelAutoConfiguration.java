package com.asyncexcel.springboot;

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
}
