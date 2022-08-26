package com.asyncexcel.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description 注解在开发自定义的导入导出逻辑中，会通过扫描作为bean被注册到子容器中
 * @Author 姚仲杰#80998699
 * @Date 2022/7/7 16:38
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelHandle {
    String name() default "";
}
