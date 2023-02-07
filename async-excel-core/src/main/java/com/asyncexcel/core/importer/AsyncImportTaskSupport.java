package com.asyncexcel.core.importer;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.asyncexcel.core.ErrorMsg;
import com.asyncexcel.core.ExceptionUtil;
import com.asyncexcel.core.ImportRow;
import com.asyncexcel.core.ImportRowMap;
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
import java.util.Map.Entry;
import java.util.Set;
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
    
    private final static Logger log = LoggerFactory.getLogger(AsyncImportTaskSupport.class);
    IStorageService storageService;
    TaskService taskService;
    public static String IMPORT_ERROR_PREFIX = "import-error-";
    public static String XLSX_SUFFIX = ".xlsx";
    
    public AsyncImportTaskSupport(IStorageService storageService,
        TaskService taskService) {
        this.storageService = storageService;
        this.taskService = taskService;
    }
    
    @Override
    public ExcelTask createTask(DataImportParam param) {
        ExcelTask task = new ExcelTask();
        task.setType(1);
        task.setStatus(0);
        task.setSourceFile(param.getSourceFile());
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
    
    /**这段逻辑过于复杂，后续需要进行抽象重构
     * @param dataList
     * @param ctx
     * @param errorMsgList
     */
    @Override
    public void onWrite(Collection<?> dataList, ImportContext ctx,
        List<ErrorMsg> errorMsgList) {
        //行数据不能为空
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        //最终写出的数据行
        List<Object> errorDataList = new ArrayList<>();
        Map<Integer, Object> map = new HashMap<>(dataList.size());
        //将数据行转map，行号为key遍历数据行，两种情况
        // 一种是格式错误，非业务错误，此时errorMsgList没有数据
        // 第二种是业务错误，此时错误消息有数据，这里又分为两种情况，一种是普通对象类型的，另一种是importRowMap类型的
        for (Object o : dataList) {
            if (o instanceof ImportRow) {
                ImportRow o1 = (ImportRow) o;
                map.put(o1.getRow(), o);
            } else {
                errorDataList.add(o);
            }
        }
        //处理上述第二种情况，将错误消息与对应行号进行匹配
        if (errorDataList.size() == 0 && errorMsgList != null && errorMsgList.size() > 0) {
            //如果是ImportRowMap再次进行处理
            if (ImportRowMap.class.isAssignableFrom(ctx.getErrorHeadClass())) {
                for (ErrorMsg errorMsg : errorMsgList) {
                    Object o = map.get(errorMsg.getRow());
                    if (o instanceof ImportRowMap) {
                        ImportRowMap o1 = (ImportRowMap) o;
                        o1.setRowFailMessage(errorMsg.getMsg());
                        Map<Integer, Cell> dataMap = o1.getDataMap();
                        Set<Entry<Integer, Cell>> entries = dataMap.entrySet();
                        List<Object> cellDatas=new ArrayList();
                        for (Entry<Integer, Cell> entry : entries) {
                            ReadCellData readCellData =(ReadCellData) entry.getValue();
                            CellDataTypeEnum type = readCellData.getType();
                            Object value;
                            switch (type){
                                case EMPTY:
                                case STRING:
                                    value=readCellData.getStringValue();
                                    break;
                                case NUMBER:
                                    value=readCellData.getNumberValue();
                                    break;
                                case BOOLEAN:
                                    value=readCellData.getBooleanValue();
                                    break;
                                case DATE:
                                    value=readCellData.getDataFormatData();
                                    break;
                                default:
                                    throw new IllegalStateException("Cannot set values now");
                            }
                            cellDatas.add(value);
                        }
                        //数据对齐表头,避免最后一列为空时，错误信息的列被挤到上一列
                        Map<Integer, String>  headMap= o1.getHeadMap();
                        int size = headMap.size();
                        if (cellDatas.size()<size) {
                            for (int i = 0; i < size - cellDatas.size(); i++) {
                                cellDatas.add(null);
                            }
                        }
                        //添加错误消息列
                        cellDatas.add(((ImportRowMap) o).getRowFailMessage());
                        cellDatas.add(((ImportRowMap) o).getRow());
                        errorDataList.add(cellDatas);
                    }else {
                        errorDataList.add(o);
                    }
                }
            }else {
                for (ErrorMsg errorMsg : errorMsgList) {
                    Object o = map.get(errorMsg.getRow());
                    if (o instanceof ImportRow) {
                        ImportRow o1 = (ImportRow) o;
                        o1.setRowFailMessage(errorMsg.getMsg());
                    }
                    errorDataList.add(o);
                }
            }
        }
        if (errorDataList.size() == 0) {
            return;
        }
        
        if (ctx.getOutputStream() == null) {
            PipedOutputStream pos = new PipedOutputStream();
            try {
                PipedInputStream pis = new PipedInputStream(pos);
                ctx.setInputStream(pis);
                StringBuilder sb = new StringBuilder();
                sb.append(IMPORT_ERROR_PREFIX)
                    .append(ctx.getFileName())
                    .append(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")))
                    .append(XLSX_SUFFIX);
                final String errFileName = sb.toString();
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
            ExcelWriterBuilder builder = EasyExcel.write(ctx.getOutputStream())
                .excelType(ExcelTypeEnum.XLSX).autoCloseStream(false);
            //判断是否是ImportRowMap的子类，如果是那么进行表头转换处理
            if (ImportRowMap.class.isAssignableFrom(ctx.getErrorHeadClass())) {
                List<List<String>> headList=new ArrayList<>();
                for (Object o : dataList) {
                    ImportRowMap rowMap = (ImportRowMap) o;
                    Set<Entry<Integer, String>> heads = rowMap.getHeadMap().entrySet();
                    for (Entry<Integer, String> head : heads) {
                        List<String> headCell = new ArrayList<>();
                        headCell.add(head.getValue());
                        headList.add(headCell);
                    }
                    break;
                }
                List<String> row = new ArrayList<>();
                List<String> failMsg=new ArrayList<>();
                row.add(SheetConst.FAIL_ROW_TITLE);
                failMsg.add(SheetConst.FAIL_MSG_TITLE);
                headList.add(failMsg);
                headList.add(row);
                builder.registerWriteHandler(new FailMsgWriteHandler());
                builder.head(headList);
            }else{
                //如果不是那么直接设置
                builder.head(ctx.getErrorHeadClass());
            }
            ExcelWriter excelWriter = builder.build();
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
        if (log.isDebugEnabled()) {
            log.debug("task import error");
        }
    }
    
    public void close(ImportContext ctx) {
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
                if (ctx.getFuture() != null) {
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
        if (log.isDebugEnabled()) {
            log.debug("task completed");
        }
    }
    
}
