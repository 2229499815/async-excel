package com.asyncexcel.springboot;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/7 16:46
 */
public class SpringExcelContext extends ExcelContextFactory {
    
    public SpringExcelContext() {
        super(ExcelContextConfiguration.class, "excelContext", "spring.excel.name");
    }
}
