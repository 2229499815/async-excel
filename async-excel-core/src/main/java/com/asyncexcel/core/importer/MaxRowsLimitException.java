package com.asyncexcel.core.importer;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/26 16:22
 */
public class MaxRowsLimitException extends ImportException {
    
    public MaxRowsLimitException() {
        super();
    }
    
    public MaxRowsLimitException(String message) {
        super(message);
    }
    
    public MaxRowsLimitException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public MaxRowsLimitException(Throwable cause) {
        super(cause);
    }
    
    protected MaxRowsLimitException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
