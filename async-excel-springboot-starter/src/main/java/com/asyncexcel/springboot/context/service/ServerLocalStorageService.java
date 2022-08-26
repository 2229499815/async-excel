package com.asyncexcel.springboot.context.service;

import com.asyncexcel.core.service.IStorageService;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/8/25 16:49
 */
public class ServerLocalStorageService implements IStorageService {
    private final static String tempPath="/tmp/upload/";
    
    @Override
    public String write(String name, Consumer<OutputStream> osConsumer) throws Exception {
        return null;
    }
    
    @Override
    public String write(String name, InputStream data) throws Exception {
        String filePath = tempPath + name;
        MockMultipartFile mockMultipartFile = new MockMultipartFile(name, data);
        File file =new File(filePath);
        if (null == file) {
            return null;
        } else {
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                try {
                    file.createNewFile();
                } catch (Exception var2) {
                    throw new IOException(var2);
                }
            }
        }
        mockMultipartFile.transferTo(file);
        return filePath;
    }
    
    @Override
    public InputStream read(String path) throws Exception {
        return null;
    }
    
    @Override
    public boolean delete(String path) throws Exception {
        return false;
    }
}
