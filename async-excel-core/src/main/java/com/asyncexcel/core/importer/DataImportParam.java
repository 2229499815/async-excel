package com.asyncexcel.core.importer;

import com.asyncexcel.core.DataParam;
import java.io.InputStream;
import lombok.Builder;
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
public class DataImportParam extends DataParam {
    
    /**
     * 输入流
     */
    private InputStream stream;
    /**
     * 文件名称
     */
    private String filename;
    /**
     * 导入对应的实体类
     */
    private Class<?> model;
    /**
     * 分批次大小，如果你导入1w条数据，每次1000会分10次读到内存中
     */
    private int batchSize = 1000;
    
    /**
     * 是否限制导入行数，默认false，如果限制行数将会出发行数限制异常，例如限制1000行，你的文件如果超过1000行将会抛异常
     */
    private boolean validMaxRows = false;
    
    /**
     * 行数限制validMaxRows=true时起作用
     */
    private int maxRows = 1000;
    
    /**
     * 是否进行表头校验，顺序单元格内容都应该与实体类保持一致。
     */
    private boolean validHead = true;
    
}
