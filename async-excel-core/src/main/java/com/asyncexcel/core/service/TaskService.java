package com.asyncexcel.core.service;

import com.asyncexcel.core.model.ExcelTask;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/8/25 14:34
 */
public interface TaskService {
    
    /**保存任务
     * @param task
     * @return
     */
    boolean save(ExcelTask task);
    
    /**根据id更新任务
     * @param task
     * @return
     */
    boolean updateById(ExcelTask task);
}
