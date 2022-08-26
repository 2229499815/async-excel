package com.asyncexcel.core;

/**
 * @Description 异常转换
 * @Author 姚仲杰#80998699
 * @Date 2022/7/13 20:26
 */
public class ExceptionUtil {
    
    public static RuntimeException wrap2Runtime(Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException(e);
    }

    public static Throwable getOriginal(Throwable e){
        Throwable ex = e.getCause();
        if(ex != null){
            return getOriginal(ex);
        }else{
            return e;
        }
    }
}
