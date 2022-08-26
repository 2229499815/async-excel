package com.asyncexcel.core.exporter;

import com.asyncexcel.core.ExportPage;
import com.asyncexcel.core.Handler;
import java.util.List;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/7 23:40
 */
public interface ExportHandler<T> extends Handler {
    
    /**一个批次导入
     * @param startPage
     * @param limit
     * @param param
     * @return
     */
    ExportPage<T> exportData(int startPage, int limit, DataExportParam param);
    
    /**一个批次导入前
     * @param param
     */
    default void beforeExportData(DataExportParam param) {
    }
    
    /**一个批次导入后
     * @param list
     */
    default void afterExportData(List<T> list) {
    }

}
