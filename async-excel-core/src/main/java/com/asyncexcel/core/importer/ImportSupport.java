package com.asyncexcel.core.importer;

import com.asyncexcel.core.ErrorMsg;
import com.asyncexcel.core.Support;
import com.asyncexcel.core.model.ExcelTask;
import java.util.Collection;
import java.util.List;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/13 15:29
 */
public interface ImportSupport extends Support {
    
    /**创建任务
     * @param param
     * @return
     */
    ExcelTask createTask(DataImportParam param);
    
    /**
     * 批次导入前
     */
    void beforeImport();
    
    /**开始执行导入
     * @param ctx
     */
    void onImport(ImportContext ctx);
    
    /**正常数据错误格式错误等写入错误文件返回下载链接
     * @param dataList
     * @param ctx
     * @param errorMsgList
     */
    void onWrite(Collection<?> dataList, ImportContext ctx, List<ErrorMsg> errorMsgList);
    
    /**失败阶段由于某写原因导致导入中断，如业务抛异常了，或者服务中断了
     * @param ctx
     */
    void onError(ImportContext ctx);
    
    /**王成阶段
     * @param ctx
     */
    void onComplete(ImportContext ctx);
}
