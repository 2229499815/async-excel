package com.asyncexcel.core.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/8/25 14:34
 */
public interface IStorageService {
    /**自定义一个输出流函数写出去
     * @param name
     * @param osConsumer
     * @return 文件路径
     * @throws Exception
     */
    String write(String name, Consumer<OutputStream> osConsumer) throws Exception;
    
    /**从一个输入流读取数据写出到另一个数据流中去
     * @param name 文件名
     * @param data 数据流
     * @return 文件路径
     * @throws Exception
     */
    String write(String name, InputStream data) throws Exception;
    
    
    /** 读文件
     * @param path
     * @return
     * @throws Exception
     */
    InputStream read(String path) throws Exception;
    
    
    /**删除文件
     * @param path
     * @return
     * @throws Exception
     */
    boolean delete(String path) throws Exception;
}
