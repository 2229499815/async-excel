package com.asyncexcel.core.importer;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.asyncexcel.core.ExcelContext;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Future;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/11 10:13
 */
public class ImportContext extends ExcelContext {
    private String fileName;
    private String sheetName="Sheet1";
    private Class<?> errorHeadClass;
    private ExcelWriter excelWriter;
    private WriteSheet writeSheet;
    private OutputStream outputStream;
    private InputStream inputStream;
    private Future<String> future;
    private String errorFile;
    private String failMessage;
    private boolean validMaxRows;
    private int maxRows=1000;
    private boolean validHead=true;
    
    public boolean isValidHead() {
        return validHead;
    }
    
    public void setValidHead(boolean validHead) {
        this.validHead = validHead;
    }
    
    public boolean isValidMaxRows() {
        return validMaxRows;
    }
    
    public void setValidMaxRows(boolean validMaxRows) {
        this.validMaxRows = validMaxRows;
    }
    
    public int getMaxRows() {
        return maxRows;
    }
    
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }
    
    public String getFailMessage() {
        return failMessage;
    }
    
    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }
    
    public Future<String> getFuture() {
        return future;
    }
    
    public void setFuture(Future<String> future) {
        this.future = future;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getErrorFile() {
        return errorFile;
    }
    
    public void setErrorFile(String errorFile) {
        this.errorFile = errorFile;
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
    
    public OutputStream getOutputStream() {
        return outputStream;
    }
    
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    public InputStream getInputStream() {
        return inputStream;
    }
    
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    
    public Class<?> getErrorHeadClass() {
        return errorHeadClass;
    }
    
    public void setErrorHeadClass(Class<?> errorHeadClass) {
        this.errorHeadClass = errorHeadClass;
    }
    
    public String getSheetName() {
        return sheetName;
    }
    
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
    
}
