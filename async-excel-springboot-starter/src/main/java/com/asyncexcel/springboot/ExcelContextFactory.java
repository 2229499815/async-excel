package com.asyncexcel.springboot;

import java.util.Collections;
import java.util.Set;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/7 21:13
 */
public class ExcelContextFactory implements DisposableBean, ApplicationContextAware {
    
    private final String propertySourceName;
    
    public static Set<String> basePackages;
    
    private final String propertyName;
    
    private final String contextName="excelContext";
    
    private AnnotationConfigApplicationContext context;
    private ApplicationContext parent;
    
    private Class<?> defaultConfigType;
    
    public ExcelContextFactory(Class<?> defaultConfigType, String propertySourceName,
        String propertyName) {
        this.defaultConfigType = defaultConfigType;
        this.propertySourceName = propertySourceName;
        this.propertyName = propertyName;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext parent) throws BeansException {
        this.parent = parent;
    }
    
    @Override
    public void destroy() {
        context.close();
    }
    
    protected AnnotationConfigApplicationContext getContext() {
        if (this.context==null) {
            synchronized (AnnotationConfigApplicationContext.class) {
                if (this.context==null) {
                    this.context=createContext();
                }
            }
        }
        return this.context;
    }
    
    protected AnnotationConfigApplicationContext createContext() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(PropertyPlaceholderAutoConfiguration.class,
            this.defaultConfigType);
        
        context.getEnvironment().getPropertySources().addFirst(new MapPropertySource(
            this.propertySourceName,
            Collections.<String, Object>singletonMap(this.propertyName, this.contextName)));
        if (this.parent != null) {
            ExcelHandleBasePackages ehbp = this.parent.getBean(ExcelHandleBasePackages.class);
            basePackages = ehbp.getBasePackages();
            context.setParent(this.parent);
            context.setClassLoader(this.parent.getClassLoader());
        }
        context.setDisplayName(generateDisplayName(this.contextName));
        context.refresh();
        return context;
    }
    
    protected String generateDisplayName(String name) {
        return this.getClass().getSimpleName() + "-" + name;
    }
    
    public <T> T getInstance(Class<T> type) {
        AnnotationConfigApplicationContext context = getContext();
        if (BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context,
            type).length > 0) {
            return context.getBean(type);
        }
        return null;
    }
}
