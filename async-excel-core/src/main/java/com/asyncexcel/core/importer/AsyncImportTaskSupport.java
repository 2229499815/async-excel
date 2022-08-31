package com.asyncexcel.core.importer;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.asyncexcel.core.ErrorMsg;
import com.asyncexcel.core.ExceptionUtil;
import com.asyncexcel.core.ImportRow;
import com.asyncexcel.core.model.ExcelTask;
import com.asyncexcel.core.service.IStorageService;
import com.asyncexcel.core.service.TaskService;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description 处理出错的数据，单独开个excel写入数据并返回一个文件地址提供下载
 * @Author 姚仲杰#80998699
 * @Date 2022/7/12 14:49
 */
public class AsyncImportTaskSupport implements ImportSupport {
    private final static Logger log= LoggerFactory.getLogger(AsyncImportTaskSupport.class);
    IStorageService storageService;
    TaskService taskService;
    public static String IMPORT_ERROR_PREFIX ="import-error-";
    public static  String XLSX_SUFFIX =".xlsx";
    
    public AsyncImportTaskSupport(IStorageService storageService,
        TaskService taskService) {
        this.storageService = storageService;
        this.taskService = taskService;
    }
    
    @Override
    public ExcelTask createTask(DataImportParam param){
        ExcelTask task=new ExcelTask();
        task.setType(1);
        task.setStatus(0);
        task.setFileName(param.getFilename());
        task.setStartTime(LocalDateTime.now());
        task.setTenantCode(param.getTenantCode());
        task.setCreateUserCode(param.getCreateUserCode());
        task.setBusinessCode(param.getBusinessCode());
        taskService.save(task);
        return task;
    }
    
    @Override
    public void beforeImport() {
    
    }
    
    @Override
    public void onImport(ImportContext ctx) {
        ExcelTask excelTask = ctx.getTask();
        excelTask.setStatus(1);
        excelTask.setFailedCount(ctx.getFailCount());
        excelTask.setSuccessCount(ctx.getSuccessCount());
        excelTask.setTotalCount(ctx.getTotalCount());
        taskService.updateById(excelTask);
    }
    
    @Override
    public void onWrite(Collection<?> dataList, ImportContext ctx,
        List<ErrorMsg> errorMsgList) {
        if (CollectionUtils.isEmpty(dataList)){
            return;
        }
        List<Object> errorDataList = new ArrayList<>();
        Map<Integer, Object> map = new HashMap<>(dataList.size());
        for (Object o : dataList) {
            if (o instanceof ImportRow) {
                ImportRow o1 = (ImportRow) o;
                map.put(o1.getRow(), o);
            }else{
                errorDataList.add(o);
            }
        }
        
        if (errorDataList.size()==0&&errorMsgList!=null&&errorMsgList.size() > 0) {
            for (ErrorMsg errorMsg : errorMsgList) {
                Object o = map.get(errorMsg.getRow());
                if (o instanceof ImportRow) {
                    ImportRow o1 = (ImportRow) o;
                    o1.setRowFailMessage(errorMsg.getMsg());
                }
                errorDataList.add(o);
            }
        }
        if(errorDataList.size()==0){
            return;
        }
        
        if (ctx.getOutputStream() == null) {
            PipedOutputStream pos = new PipedOutputStream();
            try {
                PipedInputStream pis = new PipedInputStream(pos);
                ctx.setInputStream(pis);
                StringBuilder sb=new StringBuilder();
                sb.append(IMPORT_ERROR_PREFIX)
                    .append(ctx.getFileName())
                    .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")))
                    .append(XLSX_SUFFIX);
                final String errFileName=sb.toString();
                //此处单独起线程避免线程互相等待死锁
                FutureTask<String> futureTask = new FutureTask<>(
                    () -> storageService.write(errFileName, ctx.getInputStream()));
                new Thread(futureTask).start();
                ctx.setFuture(futureTask);
            } catch (IOException e) {
                ExceptionUtil.wrap2Runtime(e);
            }
            ctx.setOutputStream(pos);
        }
        if (ctx.getExcelWriter() == null) {
            ExcelWriter excelWriter = EasyExcel.write(ctx.getOutputStream())
                .head(ctx.getErrorHeadClass())
                .excelType(ExcelTypeEnum.XLSX).autoCloseStream(false).build();
            WriteSheet writeSheet = EasyExcel.writerSheet(0).sheetName(ctx.getSheetName()).build();
            ctx.setWriteSheet(writeSheet);
            ctx.setExcelWriter(excelWriter);
        }
        ctx.getExcelWriter().write(errorDataList, ctx.getWriteSheet());
    }
    
    @Override
    public void onError(ImportContext ctx) {
        close(ctx);
        ExcelTask excelTask = ctx.getTask();
        excelTask.setStatus(3);
        excelTask.setFailedCount(ctx.getFailCount());
        excelTask.setSuccessCount(ctx.getSuccessCount());
        excelTask.setEndTime(LocalDateTime.now());
        excelTask.setTotalCount(ctx.getTotalCount());
        excelTask.setFailedFileUrl(ctx.getErrorFile());
        excelTask.setFailedMessage(ctx.getFailMessage());
        taskService.updateById(excelTask);
        if (log.isDebugEnabled()){
            log.debug("task import error");
        }
    }
    
    public void close(ImportContext ctx){
        if (ctx.getExcelWriter() != null) {
            ctx.getExcelWriter().finish();
        }
        if (ctx.getOutputStream() != null) {
            try {
                ctx.getOutputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ctx.getInputStream() != null) {
            try {
                if (ctx.getFuture()!=null) {
                    ctx.setErrorFile(ctx.getFuture().get());
                }
                ctx.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void onComplete(ImportContext ctx) {
        close(ctx);
        ExcelTask excelTask = ctx.getTask();
        excelTask.setStatus(2);
        excelTask.setFailedCount(ctx.getFailCount());
        excelTask.setSuccessCount(ctx.getSuccessCount());
        excelTask.setEndTime(LocalDateTime.now());
        excelTask.setTotalCount(ctx.getTotalCount());
        excelTask.setFailedFileUrl(ctx.getErrorFile());
        taskService.updateById(excelTask);
        if (log.isDebugEnabled()){
            log.debug("task completed");
        }
    }
    
}
