package com.asyncexcel.springboot.context.service;

import com.asyncexcel.core.model.ExcelTask;
import com.asyncexcel.core.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/8/25 18:45
 */
@Service
public class TaskManage implements TaskService {
    
    @Autowired
    IExcelTaskService excelTaskService;
    @Override
    public boolean save(ExcelTask task) {
        return excelTaskService.save(task);
    }
    
    @Override
    public boolean updateById(ExcelTask task) {
        return excelTaskService.updateById(task);
    }
}
