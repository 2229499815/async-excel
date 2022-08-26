package com.asyncexcel.core.exporter;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.asyncexcel.core.ExceptionUtil;
import com.asyncexcel.core.model.ExcelTask;
import com.asyncexcel.core.service.IStorageService;
import com.asyncexcel.core.service.TaskService;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.concurrent.FutureTask;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/15 14:39
 */
public class AsyncExportTaskSupport implements ExportSupport {
    
    private final static Logger log = LoggerFactory.getLogger(AsyncExportTaskSupport.class);
    IStorageService storageService;
    TaskService taskService;
    
    public AsyncExportTaskSupport(IStorageService storageService,
        TaskService taskService) {
        this.storageService = storageService;
        this.taskService = taskService;
    }
    
    @Override
    public ExcelTask createTask(DataExportParam param) {
        ExcelTask task = new ExcelTask();
        task.setType(2);
        task.setStatus(0);
        task.setStartTime(LocalDateTime.now());
        task.setTenantCode(param.getTenantCode());
        task.setCreateUserCode(param.getCreateUserCode());
        task.setBusinessCode(param.getBusinessCode());
        task.setFileName(param.getExportFileName());
        taskService.save(task);
        return task;
    }
    
    @Override
    public void onExport(ExportContext ctx) {
        ExcelTask excelTask = ctx.getTask();
        excelTask.setStatus(1);
        excelTask.setFailedCount(ctx.getFailCount());
        excelTask.setSuccessCount(ctx.getSuccessCount());
        excelTask.setTotalCount(ctx.getTotalCount());
        taskService.updateById(excelTask);
    }
    
    @Override
    public void onWrite(Collection<?> dataList, ExportContext ctx) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        if (ctx.getOutputStream() == null) {
            PipedOutputStream pos = new PipedOutputStream();
            try {
                PipedInputStream pis = new PipedInputStream(pos);
                ctx.setInputStream(pis);
                //此处单独起线程避免线程互相等待死锁
                FutureTask<String> futureTask = new FutureTask<>(
                        () -> storageService.write(ctx.getFileName(), ctx.getInputStream()));
                new Thread(futureTask).start();
                ctx.setFuture(futureTask);
            } catch (IOException e) {
                ExceptionUtil.wrap2Runtime(e);
            }
            ctx.setOutputStream(pos);
        }
        
        //创建excel
        if (ctx.getExcelWriter() == null) {
            ExcelWriterBuilder writerBuilder = EasyExcel.write(ctx.getOutputStream())
                .excelType(ExcelTypeEnum.XLSX).autoCloseStream(false);
            //动态表头
            if (ctx.isDynamicHead()) {
                writerBuilder.head(ctx.getHeadList());
            } else {
                writerBuilder.head(ctx.getHeadClass());
            }
            if (ctx.getWriteHandlers() != null && ctx.getWriteHandlers().size() > 0) {
                for (WriteHandler writeHandler : ctx.getWriteHandlers()) {
                    writerBuilder.registerWriteHandler(writeHandler);
                }
            }
            if (ctx.getConverters() != null && ctx.getConverters().size() > 0) {
                for (Converter<?> converter : ctx.getConverters()) {
                    writerBuilder.registerConverter(converter);
                }
            }
            ExcelWriter excelWriter = writerBuilder.build();
            WriteSheet writeSheet = EasyExcel.writerSheet(0).sheetName(ctx.getSheetName())
                .build();
            ctx.setWriteSheet(writeSheet);
            ctx.setExcelWriter(excelWriter);
        }
        
        ctx.getExcelWriter().write(dataList, ctx.getWriteSheet());
        
    }
    
    public void close(ExportContext ctx) {
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
                ctx.setResultFile(ctx.getFuture().get());
                ctx.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void onComplete(ExportContext ctx) {
        close(ctx);
        ExcelTask excelTask = ctx.getTask();
        excelTask.setStatus(2);
        excelTask.setFailedCount(ctx.getFailCount());
        excelTask.setSuccessCount(ctx.getSuccessCount());
        excelTask.setEndTime(LocalDateTime.now());
        excelTask.setTotalCount(ctx.getTotalCount());
        excelTask.setFileUrl(ctx.getResultFile());
        taskService.updateById(excelTask);
        if (log.isDebugEnabled()) {
            log.debug("task completed");
        }
    }
    
    @Override
    public void onError(ExportContext ctx) {
        close(ctx);
        ExcelTask excelTask = ctx.getTask();
        excelTask.setStatus(3);
        excelTask.setFailedCount(ctx.getFailCount());
        excelTask.setSuccessCount(ctx.getSuccessCount());
        excelTask.setEndTime(LocalDateTime.now());
        excelTask.setTotalCount(ctx.getTotalCount());
        excelTask.setFileUrl(ctx.getResultFile());
        excelTask.setFailedMessage(ctx.getFailMessage());
        taskService.updateById(excelTask);
        if (log.isDebugEnabled()) {
            log.debug("task Error");
        }
    }
}
