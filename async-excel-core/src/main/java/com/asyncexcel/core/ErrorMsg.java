package com.asyncexcel.core;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/5 10:26
 */
public class ErrorMsg {
    private Integer row;
    private String msg;
    
    public ErrorMsg(Integer row, String msg) {
        this.row = row;
        this.msg = msg;
    }
    
    public Integer getRow() {
        return row;
    }
    
    public void setRow(Integer row) {
        this.row = row;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
