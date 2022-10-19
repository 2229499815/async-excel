package com.asyncexcel.core.importer;

import com.asyncexcel.core.ErrorMsg;
import com.asyncexcel.core.Handler;
import java.util.List;


/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/5 14:14
 */
public interface ImportHandler<T> extends Handler {
    
    /**导入数据
     * @param list
     * @param param
     * @return
     * @throws Exception
     */
    List<ErrorMsg> importData(List<T> list, DataImportParam param) throws Exception;
    
    /**导入前
     * @param list
     * @param param
     * @throws Exception
     */
    default void beforePerPage(ImportContext ctx, List<T> list, DataImportParam param) throws Exception {
    }
    
    /**导入后
     * @param list
     * @param param
     * @param errorMsgList
     * @throws Exception
     */
    default void afterPerPage(ImportContext ctx, List<T> list, DataImportParam param, List<ErrorMsg> errorMsgList)
        throws Exception {
    }
}
