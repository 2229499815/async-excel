package com.asyncexcel.core.exporter;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.asyncexcel.core.ExcelContext;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/11 10:12
 */
public class ExportContext extends ExcelContext {
    private OutputStream outputStream;
    private ExcelWriter excelWriter;
    private WriteSheet writeSheet;
    private InputStream inputStream;
    private Class<?> headClass;
    private Future<String> future;
    private String resultFile;
    private String sheetName;
    private boolean dynamicHead;
    private List<List<String>> headList;
    private String fileName;
    private List<WriteHandler> writeHandlers;
    private List<Converter<?>> converters;
    private String failMessage;
    
    public List<WriteHandler> getWriteHandlers() {
        return writeHandlers;
    }
    
    public void setWriteHandlers(List<WriteHandler> writeHandlers) {
        this.writeHandlers = writeHandlers;
    }
    
    public List<Converter<?>> getConverters() {
        return converters;
    }
    
    public void setConverters(List<Converter<?>> converters) {
        this.converters = converters;
    }
    
    public boolean isDynamicHead() {
        return dynamicHead;
    }
    
    public void setDynamicHead(boolean dynamicHead) {
        this.dynamicHead = dynamicHead;
    }
    
    public List<List<String>> getHeadList() {
        return headList;
    }
    
    public void setHeadList(List<List<String>> headList) {
        this.headList = headList;
    }
    
    public String getResultFile() {
        return resultFile;
    }
    
    public void setResultFile(String resultFile) {
        this.resultFile = resultFile;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getSheetName() {
        return sheetName;
    }
    
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
    
    public Class<?> getHeadClass() {
        return headClass;
    }
    
    public void setHeadClass(Class<?> headClass) {
        this.headClass = headClass;
    }
    
    public OutputStream getOutputStream() {
        return outputStream;
    }
    
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    public ExcelWriter getExcelWriter() {
        return excelWriter;
    }
    
    public void setExcelWriter(ExcelWriter excelWriter) {
        this.excelWriter = excelWriter;
    }
    
    public WriteSheet getWriteSheet() {
        return writeSheet;
    }
    
    public void setWriteSheet(WriteSheet writeSheet) {
        this.writeSheet = writeSheet;
    }
    
    public InputStream getInputStream() {
        return inputStream;
    }
    
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    
    public Future<String> getFuture() {
        return future;
    }
    
    public void setFuture(Future<String> future) {
        this.future = future;
    }
    
    public String getFailMessage() {
        return failMessage;
    }
    
    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }
}
