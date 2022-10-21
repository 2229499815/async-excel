package com.asyncexcel.springboot;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @Description 收集注册给主容器的bean，此处使用deferredImportSelector目的是为了设置顺序晚于Springboot的
 * spring.factories加载。
 * @Author 姚仲杰#80998699
 * @Date 2022/7/7 16:53
 */
public class ExcelImportSelector implements DeferredImportSelector, Ordered {
    
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        List<String> importList=new ArrayList<String>();
        importList.add(ExcelAutoConfiguration.class.getName());
        importList.add(ExcelService.class.getName());
        return importList.toArray(new String[importList.size()]);
    }
    
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
