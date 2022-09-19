package com.asyncexcel.springboot;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.asyncexcel.core.exporter.AsyncExcelExporter;
import com.asyncexcel.core.exporter.DataExportParam;
import com.asyncexcel.core.exporter.ExportContext;
import com.asyncexcel.core.exporter.ExportHandler;
import com.asyncexcel.core.exporter.ExportSupport;
import com.asyncexcel.core.importer.AsyncExcelImporter;
import com.asyncexcel.core.importer.DataImportParam;
import com.asyncexcel.core.importer.ImportContext;
import com.asyncexcel.core.importer.ImportHandler;
import com.asyncexcel.core.importer.ImportSupport;
import com.asyncexcel.core.model.ExcelTask;
import com.asyncexcel.springboot.context.service.IExcelTaskService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/8 0:07
 */
public class ExcelService {
    private final static Logger log= LoggerFactory.getLogger(ExcelService.class);
    
    @Autowired
    ExcelThreadPool excelThreadPool;
    
    @Autowired
    SpringExcelContext context;
    public Long doImport(Class<? extends ImportHandler> cls, DataImportParam param){
        ImportHandler handler = context.getInstance(cls);
        ImportSupport support = context.getInstance(ImportSupport.class);
        ExcelTask task = support.createTask(param);
        log.info("添加任务taskId:{}",task.getId());
        ImportContext ctx=new ImportContext();
        ctx.setTask(task);
        ctx.setFileName(param.getFilename());
        ctx.setErrorHeadClass(param.getModel());
        ctx.setValidMaxRows(param.isValidMaxRows());
        ctx.setMaxRows(param.getMaxRows());
        ctx.setValidHead(param.isValidHead());
        AsyncExcelImporter asyncExcelImporter = new AsyncExcelImporter(excelThreadPool.getExecutor());
        asyncExcelImporter.importData(handler,support,param,ctx);
        return task.getId();
    }
    
    public Long doExport(Class<? extends ExportHandler> cls, DataExportParam param){
        String filePrefix="导出";
        ExportHandler handler = context.getInstance(cls);
        ExportSupport support=context.getInstance(ExportSupport.class);
        ExcelTask task = support.createTask(param);
        ExportContext ctx=new ExportContext();
        ctx.setTask(task);
        ctx.setHeadClass(param.getHeadClass());
        ctx.setDynamicHead(param.isDynamicHead());
        ctx.setHeadList(param.getHeadList());
        ctx.setWriteHandlers(param.getWriteHandlers());
        ctx.setConverters(param.getConverters());
        ctx.setSheetName(param.getSheetName());
        String fileName=param.getExportFileName();
        StringBuilder sb=new StringBuilder(filePrefix).append(task.getId()).append("-");
        if (StringUtils.isEmpty(fileName)){
            sb.append(ExcelTypeEnum.XLSX.getValue());
        }else {
            if (fileName.lastIndexOf(".")!=-1){
                String extension = fileName.substring(fileName.lastIndexOf("."));
                if (!ExcelTypeEnum.XLSX.getValue().equals(extension)) {
                    sb.append(fileName).append(ExcelTypeEnum.XLSX.getValue());
                }else{
                    sb.append(fileName);
                }
            }else{
                sb.append(fileName).append(ExcelTypeEnum.XLSX.getValue());
            }
        }
        ctx.setFileName(sb.toString());
        AsyncExcelExporter asyncExcelExporter=new AsyncExcelExporter(excelThreadPool.getExecutor());
        asyncExcelExporter.exportData(handler,support,param,ctx);
        return task.getId();
    }
    
    /**提供给外部定制划按权限分页查询
     * @param task
     * @return
     */
    public IPage<ExcelTask> listPage(ExcelTask task,int currentPage,int limit){
        IExcelTaskService excelTaskService = context.getInstance(IExcelTaskService.class);
        LambdaQueryWrapper<ExcelTask> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.orderByDesc(ExcelTask::getId);
        queryWrapper.eq(!StringUtils.isEmpty(task.getId()),ExcelTask::getId,task.getId());
        queryWrapper.eq(!StringUtils.isEmpty(task.getBusinessCode()),ExcelTask::getBusinessCode,task.getBusinessCode());
        queryWrapper.eq(!StringUtils.isEmpty(task.getCreateUserCode()),ExcelTask::getCreateUserCode,task.getCreateUserCode());
        queryWrapper.eq(!StringUtils.isEmpty(task.getTenantCode()),ExcelTask::getTenantCode,task.getTenantCode());
        IPage<ExcelTask> pageParam=new Page<>(currentPage,limit);
        IPage<ExcelTask> page = excelTaskService.page(pageParam,queryWrapper);
        return page;
    }
}
