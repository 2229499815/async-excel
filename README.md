
# async-excel
一个基于easyexcel大数据量数据导入导出异步处理组件，如果你觉得对你有帮助，请点击右上角的star，支持下

## asyncexcel介绍
 
+ 1、asyncexcel基于阿里的easyexcel包装，抽取异步骨架，不改变easyexcel的特性

支持的功能列表：
+ 支持线程池外部声明，可传入SystemContext;
+ 仅支持单行表头
+ 支持表头校验
+ 支持格式转换错误校验出错写入错误文件
+ 支持业务错误写出错误文件
+ 支持行数限制
+ 支持不分页事务
+ 支持查看进度
+ 支持异步分批次导入导出，分页大小可自定义
+ 支持动态表头导出
+ 支持多租户隔离
+ 支持多模块隔离
+ 支持用户权限隔离
+ 支持自定义存储，如果不设置默认使用本地存储，存储路径/tmp/upload 如果自定义只要实现接口IStorageService 实现String write(String name, InputStream data)方法即可 声明成bean即可

### 快速开始
引入starter
```xml
<dependency>
  <groupId>com.asyncexcel</groupId>
  <artifactId>async-excel-springboot-starter</artifactId>
  <version>1.0.0</version>
</dependency>
<!--mybatis-plus-->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.2</version>
</dependency>
<!--mysql驱动-->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.29</version>
</dependency>
```
导入数据库
```sql
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
```
配置数据源(此处为多数据源，使用了spring 父子容器技术，所以不影响你原本的数据源)
```properties
#asyncexcel 数据源
spring.excel.datasource.url=jdbc:mysql://localhost:3306/async-excel?serverTimezone=GMT%2B8&autoReconnect=true&allowMultiQueries=true&useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&&useCursorFetch=true&&rewriteBatchedStatements=true
spring.excel.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.excel.datasource.password=root
spring.excel.datasource.username=root
#业务数据源
spring.datasource.url=jdbc:mysql://localhost:3306/async-excel-sample?serverTimezone=GMT%2B8&autoReconnect=true&allowMultiQueries=true&useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&&useCursorFetch=true&&rewriteBatchedStatements=true
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.password=root
spring.datasource.username=root
```
使用@EnableAsyncExcel注解启用配置
```java
@SpringBootApplication
@EnableAsyncExcel
@MapperScan({"com.asyncexcel.sample.mapper"})
public class AsyncExcelSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsyncExcelSampleApplication.class, args);
    }

}
```
编写极简示例  [示例项目 async-excel-sample](https://github.com/2229499815/async-excel-sample) 欢迎添加使用示例
```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    ExcelService excelService;

    //导入最简示例
    @PostMapping("/imports")
    public Long imports(@RequestBody MultipartFile file) throws Exception{
        DataImportParam dataImportParam = new DataImportParam()
            .setStream(file.getInputStream())
            .setModel(UserImportModel.class)
            .setBatchSize(3)
            .setFilename("用户导入");
        Long taskId = excelService.doImport(UserImportHandler.class, dataImportParam);
        return taskId;
    }
    
    //导出最简示例
    @PostMapping("/exports")
    public Long exports(){
        DataExportParam dataExportParam=new DataExportParam()
            .setExportFileName("用户导出")
            .setLimit(5)
            .setHeadClass(UserExportModel.class);
        return excelService.doExport(UserExportHandler.class,dataExportParam);
    }
    
}
```
导入导出model
```java
@Data
public class UserExportModel extends ExportRow {
    
    @ExcelProperty("用户编码")
    private String userCode;
    
    @ExcelProperty("用户姓名")
    private String userName;
    
    @ExcelProperty("手机号")
    private String mobile;
    
    @ExcelProperty("备注")
    private String remarks;

}
```
```java
@Data
public class UserImportModel extends ImportRow {
    
    @ExcelProperty("用户编码")
    private String userCode;
    
    @ExcelProperty("用户姓名")
    private String userName;
    
    @ExcelProperty("手机号")
    private String mobile;
    
    @ExcelProperty("备注")
    private String remarks;
}

```
编写导入导出处理类
```java
@ExcelHandle
public class UserExportHandler implements ExportHandler<UserExportModel> {
    
    @Autowired
    IUserService userService;
    @Override
    public ExportPage<UserExportModel> exportData(int startPage, int limit, DataExportParam dataExportParam) {
        IPage<User> iPage = new Page<>(startPage, limit);
        IPage page = userService.page(iPage);
        List<UserExportModel> list = ExportListUtil.transform(page.getRecords(), UserExportModel.class);
        ExportPage<UserExportModel> result = new ExportPage<>();
        result.setTotal(page.getTotal());
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        result.setRecords(list);
        return result;
    }
}
```
```java
@ExcelHandle
public class UserImportHandler implements ImportHandler<UserImportModel> {
    
    @Autowired
    IUserService userService;
    
    @Override
    public List<ErrorMsg> importData(List<UserImportModel> list, DataImportParam dataImportParam)
        throws Exception {
        List<ErrorMsg> errorList=new ArrayList<>();
        List<User> saveUsers=new ArrayList<>();
        for (UserImportModel userImportModel : list) {
            if (userImportModel.getMobile().contains("00000000")){
                ErrorMsg msg = new ErrorMsg(userImportModel.getRow(), "手机号包含太多0");
                errorList.add(msg);
            }else{
                BeanCopier beanCopier = BeanCopier.create(UserImportModel.class, User.class, false);
                User user = new User();
                beanCopier.copy(userImportModel,user,null);
                saveUsers.add(user);
            }
        }
        userService.saveBatch(saveUsers);
        return errorList;
    }
}
```
编写前端页面
![按钮](https://github.com/2229499815/async-excel/blob/master/doc/main.png)
![查看任务](https://github.com/2229499815/async-excel/blob/master/doc/viewtask.png)
### 高级功能
#### 自定义存储
假如你已经对接好了第三方的存储比如oss、cos，七牛云存储等
你只需要在你的项目中实现IStorageService 接口即可
友情链接：存储可以引入!梦想大佬的sdk【[spring-file-storage](https://github.com/1171736840/spring-file-storage)】 全平台支持
```java
@Component
public class CosStorageService implements IStorageService {
    @Autowired
    private CosClient cosClient;

    @Override
    public String write(String name, Consumer<OutputStream> osConsumer) throws Exception {
        return null;
    }
    //实现此方法即可
    @Override
    public String write(String name, InputStream data) throws Exception {
        String url = cosClient.upload(name,data);
        return url;
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
```
#### 自定义线程池
你可以直接使用spring的线程池，如果你需要传入你系统自定义的上下文你只需要做如下配置即可
前提是你已经定义好spring的线程池并填充好上下文装饰器ContextDecorator,线程装饰器的目的是为了将主线程的上下文传递给子线程
```java
@Configuration
@EnableAsync()
public class ContextDecoratorThreadPoolConfiguration {
    
    @Bean
    ThreadPoolTaskExecutor threadPoolTaskExecutor(){
        int core = Runtime.getRuntime().availableProcessors();
        if (core<2){
            core=2;
        }
        int corePool=core*2;
        int maxPool=core*10;
        
        ThreadPoolTaskExecutor threadPoolTaskExecutor=new ThreadPoolTaskExecutor();
        //核心线程数
        threadPoolTaskExecutor.setCorePoolSize(corePool);
        //最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(maxPool);
        //设置线程装饰器
        threadPoolTaskExecutor.setTaskDecorator(new ContextDecorator());
        //设置阻塞队列容量
        threadPoolTaskExecutor.setQueueCapacity(1000);
        //设置线程前缀
        threadPoolTaskExecutor.setThreadNamePrefix("system-context-async-");
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
    
}
```
配置线程池，如果你未配置系统将声明一个默认的线程池DefaultThreadPoolConfiguration
```java
@Configuration
@AutoConfigureBefore(ExcelAutoConfiguration.class)
@ConditionalOnClass(ExcelThreadPool.class)
public class AsyncExcelConfiguration {
    
    @Bean
    public ExcelThreadPool excelThreadPool(ThreadPoolTaskExecutor threadPoolTaskExecutor){
        return new ExcelThreadPool(threadPoolTaskExecutor.getThreadPoolExecutor());
    }
    
}
```

#### 导入单页处理场景
 此时我们可以开启最大行数校验
 dataImportParam.setMaxRows=1000;
 dataImportParam.setValidMaxRows=true;
 dataImportParam.setBatchSize=1000;
 这样就变成单页处理。

#### 导出动态表头
 dataExportParam.setDynamicHead=true;
 此时我们需要传入一个动态表头
 dataExportParam.setHeadList=list<List<String>>;
 
#### 导出自定义样式
 dataExportParam.setWriteHandlers=List<WriterHandler>
 
#### 权限隔离
表中内置三个权限隔离字段
 + `tenant_code` '租户编码'
 + `create_user_code` '用户编码'
 + `business_code` '业务编码'
可以在插入数据时进行带入，如果你声明了自定义线程池，可以从系统上下文读取对应字段设置进去。
DataParam.setxxx 系统将会默认插入数据库，不用在进行特殊处理。后续查询时你想根据什么维度查询都可以，businessCode用于区分不同的业务模块比如用户模块，订单模块可以定一个枚举进行区分
权限可以隔离到用户，也可以隔离到租户，根据你系统的要求自行定义。使用ExcelService.listPage进行数据查询。当然你也可以自定义接口的方式根据你喜欢的方式进行查询

#### 更新日志
建议使用最新版本

1.1.2版本
#bug修复

1.1.1版本
#添加多sheet导出支持
#添加动态表头导入
#添加动态表头导出
#重构导入导出将writeSheet移动至handler中在init阶段进行定义。












