package com.asyncexcel.core.exporter;

import com.asyncexcel.core.Support;
import com.asyncexcel.core.model.ExcelTask;
import java.util.Collection;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/14 19:40
 */
public interface ExportSupport extends Support {
    
    /**创建任务
     * @param param
     * @return
     */
    ExcelTask createTask(DataExportParam param);
    
    /** 导出阶段
     * @param ctx
     */
    void onExport(ExportContext ctx);
    
    /** 写文件阶段
     * @param dataList
     * @param ctx
     */
    void onWrite(Collection<?> dataList, ExportContext ctx);
    
    /**完成阶段
     * @param ctx
     */
    void onComplete(ExportContext ctx);
    
    /**失败处理阶段
     * @param ctx
     */
    void onError(ExportContext ctx);
}
