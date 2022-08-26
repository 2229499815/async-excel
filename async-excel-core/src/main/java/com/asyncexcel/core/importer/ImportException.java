package com.asyncexcel.core.importer;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/8/2 10:10
 */
public class ImportException extends RuntimeException {
    
    public ImportException() {
        super();
    }
    
    public ImportException(String message) {
        super(message);
    }
    
    public ImportException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ImportException(Throwable cause) {
        super(cause);
    }
    
    protected ImportException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
