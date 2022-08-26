package com.asyncexcel.springboot.context.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/15 11:12
 */
public class MockMultipartFile {
    private final String name;
    private String originalFilename;
    @Nullable
    private String contentType;
    private final byte[] content;
    
    public MockMultipartFile(String name, @Nullable byte[] content) {
        this(name, "", (String)null, (byte[])content);
    }
    
    public MockMultipartFile(String name, InputStream contentStream) throws IOException {
        this(name, "", (String)null, (byte[])FileCopyUtils.copyToByteArray(contentStream));
    }
    
    public MockMultipartFile(String name, @Nullable String originalFilename, @Nullable String contentType, @Nullable byte[] content) {
        Assert.hasLength(name, "Name must not be null");
        this.name = name;
        this.originalFilename = originalFilename != null ? originalFilename : "";
        this.contentType = contentType;
        this.content = content != null ? content : new byte[0];
    }
    
    public MockMultipartFile(String name, @Nullable String originalFilename, @Nullable String contentType, InputStream contentStream) throws IOException {
        this(name, originalFilename, contentType, FileCopyUtils.copyToByteArray(contentStream));
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getOriginalFilename() {
        return this.originalFilename;
    }
    
    @Nullable
    public String getContentType() {
        return this.contentType;
    }
    
    public boolean isEmpty() {
        return this.content.length == 0;
    }
    
    public long getSize() {
        return (long)this.content.length;
    }
    
    public byte[] getBytes() throws IOException {
        return this.content;
    }
    
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.content);
    }
    
    public void transferTo(File dest) throws IOException, IllegalStateException {
        FileCopyUtils.copy(this.content, dest);
    }
}
