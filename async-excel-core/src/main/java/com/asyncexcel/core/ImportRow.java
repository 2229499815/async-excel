package com.asyncexcel.core;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import java.util.Objects;
import org.apache.poi.ss.usermodel.Font;

/**
 * 所有excel实体继承这个实体
 * @author: 姚仲杰
 * @since 2022-07-012
 */
public class ImportRow implements ISheetRow {
    
    @ContentFontStyle(color = Font.COLOR_RED)
    @ExcelProperty("失败原因")
    String rowFailMessage;
    @ExcelIgnore
    private int sheetIndex;
    @ContentFontStyle(color = Font.COLOR_RED)
    @ExcelProperty("原文件行号")
    private int row;
    
    public String getRowFailMessage() {
        return rowFailMessage;
    }
    
    public void setRowFailMessage(String rowFailMessage) {
        this.rowFailMessage = rowFailMessage;
    }
    
    @Override
    public int getSheetIndex() {
        return sheetIndex;
    }
    @Override
    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }
    @Override
    public int getRow() {
        return row;
    }
    @Override
    public void setRow(int row) {
        this.row = row;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImportRow row1 = (ImportRow) o;
        return sheetIndex == row1.sheetIndex && row == row1.row;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sheetIndex, row);
    }
}
