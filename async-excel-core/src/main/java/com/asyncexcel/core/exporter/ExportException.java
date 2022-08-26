package com.asyncexcel.core.exporter;

/**
 * @Description 导出异常
 * @Author 姚仲杰#80998699
 * @Date 2022/8/2 10:08
 */
public class ExportException extends RuntimeException {
    
    public ExportException() {
        super();
    }
    
    public ExportException(String message) {
        super(message);
    }
    
    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ExportException(Throwable cause) {
        super(cause);
    }
    
    protected ExportException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
