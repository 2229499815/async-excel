package com.asyncexcel.core.importer;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.holder.xlsx.XlsxReadSheetHolder;
import com.alibaba.excel.util.ConverterUtils;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.excel.util.StringUtils;
import com.asyncexcel.core.ISheetRow;
import com.asyncexcel.core.ImportRowMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/12 17:38
 */
public class AsyncPageReadListener<T> implements ReadListener<T> {
    
    private int batchSize = 100;
    private List<T> cachedDataList = ListUtils.newArrayListWithExpectedSize(batchSize);
    private final Consumer<List<T>> consumer;
    private ImportContext ctx;
    private Map<Integer, String> headMap;
    private ImportSupport support;
    
    public AsyncPageReadListener(Consumer<List<T>> consumer,ImportSupport support, ImportContext ctx,
        int batchSize) {
        if (batchSize > 0) {
            this.batchSize = batchSize;
        }
        this.ctx = ctx;
        this.consumer = consumer;
        this.support = support;
    }
    
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> readHead, AnalysisContext context) {
        // todo 表头校验,这里可以从数据库获取
        this.headMap = ConverterUtils.convertToStringMap(readHead, context);
        if (ctx.isValidHead()){
            if (headMap.size()==0){
                throw new HeadCheckException("表头不能为空");
            }
            Map<Integer, String> headContentMap = HeadContentUtil
                .declaredFieldHeadContentMap(context.readSheetHolder().getClazz());
            if (headContentMap.size()==0) {
                throw new HeadCheckException("表头错误联系管理员");
            }
            
            StringBuilder sb=new StringBuilder();
            headMap.forEach((k,v)->{
                sb.append(v).append(",");
            });
            sb.replace(sb.lastIndexOf(","),sb.length(),"");
            String readHeadString = sb.toString();
            sb.replace(0,sb.length(),"");
            headContentMap.forEach((k,v)->{
                sb.append(v).append(",");
            });
            sb.replace(sb.lastIndexOf(","),sb.length(),"");
            String confHeadString=sb.toString();
            if (readHeadString!=null&&!readHeadString.equals(confHeadString)){
                throw new HeadCheckException("表头校验失败,表头格式为：{"+confHeadString+"}");
            }
        }
    }
    
    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        // todo 一些前置条件配置校验
    }
    
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        // todo 处理异常 数据转换异常处理，是否忽略等
        if (exception instanceof MaxRowsLimitException){
            throw new MaxRowsLimitException(exception.getMessage());
        }else if (exception instanceof ExcelDataConvertException){
            int col = 0,row =0;
            String title = "";
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException)exception;
            col = excelDataConvertException.getColumnIndex();
            row = excelDataConvertException.getRowIndex();
            title = this.headMap.get(col);
            Map<Integer, ReadCellData<?>> cellMap = (Map<Integer, ReadCellData<?>>)context.readRowHolder().getCurrentRowAnalysisResult();
            Map<Integer, String> cellStringMap = ConverterUtils
                .convertToStringMap(cellMap, context);
            //数据对齐表头
            if (cellStringMap.size()<headMap.size()) {
                for (int i = 0; i < headMap.size() - cellStringMap.size(); i++) {
                    cellStringMap.put(cellStringMap.size(),null);
                }
            }
            cellStringMap.put(cellStringMap.size(),"格式错误,{列: "+title+" }");
            cellStringMap.put(cellStringMap.size(),row+"");
            List<String> rowList = cellStringMap.values().stream().collect(Collectors.toList());
            List<List<String>> lists = new ArrayList<>();
            lists.add(rowList);
            ctx.record(1,1);
            this.support.onWrite(lists,ctx,null);
        }else if(exception instanceof HeadCheckException){
            throw new HeadCheckException(exception.getMessage());
        }
    }
    
    @Override
    public void invoke(T data, AnalysisContext context) {
        if (StringUtils.isBlank(ctx.getSheetName())) {
            String sheetName = ((XlsxReadSheetHolder) context
                .currentReadHolder()).getSheetName();
            ctx.setSheetName(sheetName);
        }
        Integer rowIndex = context.readRowHolder().getRowIndex();
        //20221025 添加动态表头支持
        if (data instanceof ImportRowMap){
            ImportRowMap rowMap = (ImportRowMap) data;
            rowMap.setDataMap(context.readRowHolder().getCellMap());
            rowMap.setHeadMap(headMap);
        }
        
        if (data instanceof ISheetRow) {
            ISheetRow rowData = (ISheetRow) data;
            rowData.setRow(rowIndex);
        } else {
            throw new RuntimeException("导入对应实体必须继承ISheetRow");
        }
        if (ctx.getTask().getEstimateCount() == null) {
            Integer headRowNumber = context.readSheetHolder().getHeadRowNumber();
            Integer totalCount = context.getTotalCount() - headRowNumber;
            ctx.getTask().setEstimateCount(totalCount.longValue());
        }
        if (ctx.isValidMaxRows()){
            if (ctx.getTask().getEstimateCount()>ctx.getMaxRows()){
                throw new MaxRowsLimitException("行数限制{"+ctx.getMaxRows()+"}行,包含表头与空行");
            }
        }
        cachedDataList.add(data);
        if (cachedDataList.size() >= this.batchSize) {
            consumer.accept(cachedDataList);
            cachedDataList = ListUtils.newArrayListWithExpectedSize(this.batchSize);
        }
    }
    
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (CollectionUtils.isNotEmpty(cachedDataList)) {
            consumer.accept(cachedDataList);
        }
    }
}
