package com.asyncexcel.springboot;

import java.util.concurrent.ExecutorService;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/8/24 13:55
 */
public class ExcelThreadPool {
    private ExecutorService executor;
    
    public ExcelThreadPool(ExecutorService executor) {
        this.executor = executor;
    }
    
    public ExecutorService getExecutor() {
        return executor;
    }
    
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }
}
