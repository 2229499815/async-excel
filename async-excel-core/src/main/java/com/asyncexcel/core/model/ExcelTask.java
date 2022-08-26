package com.asyncexcel.core.model;


import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 导入导出任务
 * </p>
 *
 * @author 姚仲杰
 * @since 2022-07-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ExcelTask implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Long id;
    
    /**
     * 类型：1-导入,2-导出
     */
    private Integer type;
    
    /**
     * 状态：0-初始,1-进行中,2-完成,3-失败
     */
    private Integer status;
    
    /**
     * 预估记录数 可能包含空行数据不准确，但是大部分情况时准确的
     */
    private Long estimateCount;
    
    /**
     * 实际总记录数 为成功记录数+失败记录数
     */
    private Long totalCount;
    
    /**
     * 成功记录数
     */
    private Long successCount;
    
    /**
     * 失败记录数
     */
    private Long failedCount;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 成功文件路径
     */
    private String fileUrl;
    
    /**
     * 失败文件路径
     */
    private String failedFileUrl;
    
    /**
     * 失败消息
     */
    private String failedMessage;
    
    /**
     * 导入开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 导入结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 租户编码，用于权限控制
     */
    private String tenantCode;
    
    /**
     * 用户编码，用于权限控制
     */
    private String createUserCode;
    
    /**
     * 业务编码 例如user,product,用于区分不同模块的导入
     */
    private String businessCode;
}
