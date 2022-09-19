drop table if exists excel_task;
CREATE TABLE `excel_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `type` tinyint(2) NOT NULL COMMENT '类型：1-导入,2-导出',
  `status` tinyint(2) NOT NULL DEFAULT 0 COMMENT '状态：0-初始,1-进行中,2-完成,3-失败',
  `estimate_count` bigint(20) NOT NULL DEFAULT 0 COMMENT '预估总记录数',
  `total_count` bigint(20) NOT NULL DEFAULT 0 COMMENT '实际总记录数',
  `success_count` bigint(20) NOT NULL DEFAULT 0 COMMENT '成功记录数',
  `failed_count` bigint(20) NOT NULL DEFAULT 0 COMMENT '失败记录数',
  `file_name` varchar(200) DEFAULT NULL COMMENT '文件名',
  `file_url` varchar(500) DEFAULT NULL COMMENT '文件路径',
  `failed_file_url` varchar(500) DEFAULT NULL COMMENT '失败文件路径',
  `failed_message` varchar(255) DEFAULT NULL COMMENT '失败消息',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `tenant_code` varchar(50) default NULL COMMENT '租户编码',
  `create_user_code` varchar(50) default NULL COMMENT '用户编码',
  `business_code` varchar(50) default NULL COMMENT '业务编码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='导入导出任务';
