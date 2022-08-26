package com.asyncexcel.springboot;

import java.util.Set;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/7 23:10
 */
public class ExcelHandleBasePackages {
    private Set<String> basePackages;
    
    public ExcelHandleBasePackages(Set<String> basePackages){
        this.basePackages=basePackages;
    }
    
    public Set<String> getBasePackages(){
        return basePackages;
    }
}
