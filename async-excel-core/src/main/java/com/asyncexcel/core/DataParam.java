package com.asyncexcel.core;

import java.util.Map;
import lombok.Data;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/8/25 14:40
 */
@Data
public class DataParam {
    private Map<String, Object> parameters;
    private String tenantCode;
    private String createUserCode;
    private String businessCode;
}
