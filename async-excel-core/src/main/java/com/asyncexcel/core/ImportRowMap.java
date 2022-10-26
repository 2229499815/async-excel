package com.asyncexcel.core;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.metadata.data.ReadCellData;
import java.util.Map;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/10/25 15:48
 */
public class ImportRowMap extends ImportRow {
    
    @ExcelIgnore
    private Map<Integer,String> headMap;
    
    @ExcelIgnore
    private Map<Integer, Cell> dataMap;
    
    public Map<Integer,String> getHeadMap() {
        return headMap;
    }
    
    public void setHeadMap(Map<Integer,String> headMap) {
        this.headMap = headMap;
    }
    
    public Map<Integer, Cell>getDataMap() {
        return dataMap;
    }
    
    public void setDataMap(Map<Integer, Cell> dataMap) {
        this.dataMap = dataMap;
    }
}
