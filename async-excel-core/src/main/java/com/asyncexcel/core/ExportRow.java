package com.asyncexcel.core;

import com.alibaba.excel.annotation.ExcelIgnore;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/18 16:09
 */
public class ExportRow implements ISheetRow {
    
    @ExcelIgnore
    private int row;
    
    @Override
    public int getRow() {
        return row;
    }
    
    @Override
    public void setRow(int row) {
        this.row = row;
    }
}
