package com.asyncexcel.core;

public interface ISheetIndex {
    
    default void setSheetIndex(int sheetIndex) {}
    
    default int getSheetIndex() {
        return 0;
    }
}
