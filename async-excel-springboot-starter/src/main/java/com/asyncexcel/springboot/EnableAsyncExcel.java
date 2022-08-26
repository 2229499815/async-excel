package com.asyncexcel.springboot;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/5 17:20
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ExcelImportSelector.class,ExcelHandleBasePackagesRegistrar.class})
public @interface EnableAsyncExcel {
    String[] basePackages() default {};
}
