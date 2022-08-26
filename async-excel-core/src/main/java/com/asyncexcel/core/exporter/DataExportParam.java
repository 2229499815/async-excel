package com.asyncexcel.core.exporter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.write.handler.WriteHandler;
import com.asyncexcel.core.DataParam;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/5 15:14
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
public class DataExportParam<T> extends DataParam {
    
    /**
     * 分页大小
     */
    private int limit=1000;
    /**
     * 导出文件名称
     */
    private String exportFileName;
    /**
     * 写入excel的sheetName
     */
    private String sheetName;
    /**
     * 是否动态表头，默认false。
     */
    private boolean dynamicHead;
    /**
     * 当dynamicHead=true时需要传一个动态表头进来
     */
    private List<List<String>> headList;
    /**
     * 表头对应的实体类
     */
    private Class<?> headClass;
    /**
     * 自定义写处理器为了，自定义样式，表格合并之类的easyExcel原生扩展
     */
    private List<WriteHandler> writeHandlers;
    /**
     * 自定义类型转换器easyExcel原生扩展
     */
    private List<Converter<?>> converters;
}
