package com.asyncexcel.springboot.context.service;

import com.asyncexcel.core.model.ExcelTask;
import com.asyncexcel.springboot.context.mapper.ExcelTaskMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 导入导出任务 服务实现类
 * </p>
 *
 * @author 姚仲杰
 * @since 2022-07-05
 */
@Service
public class ExcelTaskServiceImpl extends ServiceImpl<ExcelTaskMapper, ExcelTask> implements
    IExcelTaskService  {

}
