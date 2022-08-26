package com.asyncexcel.core.importer;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/8/22 16:28
 */
public class DataConvertException extends ImportException {
    
    public DataConvertException() {
        super();
    }
    
    public DataConvertException(String message) {
        super(message);
    }
    
    public DataConvertException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DataConvertException(Throwable cause) {
        super(cause);
    }
    
    protected DataConvertException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
