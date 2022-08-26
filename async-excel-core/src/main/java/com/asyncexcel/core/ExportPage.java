package com.asyncexcel.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 导出分页
 * @Author 姚仲杰#80998699
 * @Date 2022/7/18 11:37
 */
public class ExportPage<T> {
    private Long total;
    private Long size;
    private Long current;
    
    List<T> records=new ArrayList<>();
    
    public List<T> getRecords() {
        return records;
    }
    
    public void setRecords(List<T> records) {
        this.records = records;
    }
    
    public Long getTotal() {
        return total;
    }
    
    public void setTotal(Long total) {
        this.total = total;
    }
    
    public Long getSize() {
        return size;
    }
    
    public void setSize(Long size) {
        this.size = size;
    }
    
    public Long getCurrent() {
        return current;
    }
    
    public void setCurrent(Long current) {
        this.current = current;
    }
}
