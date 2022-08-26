package com.asyncexcel.springboot.context.mybatisplus;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/5 16:45
 */
@ConfigurationProperties("spring.excel.datasource")
public class ExcelDataSourceProperties {
    
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    
    public String getDriverClassName() {
        return driverClassName;
    }
    
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    
}
