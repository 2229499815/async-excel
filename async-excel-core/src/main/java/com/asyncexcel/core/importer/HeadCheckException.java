package com.asyncexcel.core.importer;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/27 16:12
 */
public class HeadCheckException extends ImportException {
    
    public HeadCheckException() {
        super();
    }
    
    public HeadCheckException(String message) {
        super(message);
    }
    
    public HeadCheckException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public HeadCheckException(Throwable cause) {
        super(cause);
    }
    
    protected HeadCheckException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
