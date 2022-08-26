package com.asyncexcel.springboot;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * @Description 由于跨容器，所以通过EnableAsyncExcel注解将扫描的包基础路径传入子容器
 * @Author 姚仲杰#80998699
 * @Date 2022/7/7 23:09
 */
public class ExcelHandleBasePackagesRegistrar implements ImportBeanDefinitionRegistrar {
    
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
        BeanDefinitionRegistry registry) {
        Set<String> basePackages = getBasePackages(importingClassMetadata);
        BeanDefinitionBuilder definition = BeanDefinitionBuilder
            .genericBeanDefinition(ExcelHandleBasePackages.class);
        definition.addConstructorArgValue(basePackages);
        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        String name = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, registry);
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition,name);
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }
    
    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
            .getAnnotationAttributes(EnableAsyncExcel.class.getCanonicalName());
        
        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        
        if (basePackages.isEmpty()) {
            basePackages.add(
                ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }
}
