package com.asyncexcel.core;


import com.asyncexcel.core.model.ExcelTask;

/**
 * @Description 导入导出上下文
 * @Author 姚仲杰#80998699
 * @Date 2022/7/15 14:42
 */
public class ExcelContext {
    private ExcelTask task;
    private Long totalCount=0L;
    private Long failCount=0L;
    private Long successCount=0L;
    
    public ExcelTask getTask() {
        return task;
    }
    
    public void setTask(ExcelTask task) {
        this.task = task;
    }
    
    public void record(int dataSize){
        record(dataSize,0);
    }
    
    public void record(int dataSize,int errorSize){
        this.totalCount=this.totalCount+dataSize;
        this.successCount=this.successCount+dataSize-errorSize;
        this.failCount=this.failCount+errorSize;
    }
    
    public Long getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
    
    public Long getFailCount() {
        return failCount;
    }
    
    public void setFailCount(Long failCount) {
        this.failCount = failCount;
    }
    
    public Long getSuccessCount() {
        return successCount;
    }
    
    public void setSuccessCount(Long successCount) {
        this.successCount = successCount;
    }
}
