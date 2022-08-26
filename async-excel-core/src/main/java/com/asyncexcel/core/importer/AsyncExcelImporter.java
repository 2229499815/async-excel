package com.asyncexcel.core.importer;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.asyncexcel.core.ErrorMsg;
import com.asyncexcel.core.ExceptionUtil;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/11 10:14
 */
public class AsyncExcelImporter {
    
    private final static Logger log = LoggerFactory.getLogger(AsyncExcelImporter.class);
    ExecutorService executor;
    
    public AsyncExcelImporter(ExecutorService executor) {
        this.executor = executor;
    }
    
    public <T> void importData(ImportHandler<T> handler, ImportSupport support,
        DataImportParam param,
        ImportContext ctx) {
        Consumer<List<T>> consumer = (dataList -> {
            support.onImport(ctx);
            try {
                handler.beforeImportData(dataList, param);
                List<ErrorMsg> errorList = handler.importData(dataList, param);
                ctx.record(dataList.size(), errorList.size());
                support.onWrite(dataList, ctx, errorList);
                handler.afterImportData(dataList, param, errorList);
            } catch (Exception e) {
                log.error("导入过程异常");
                if (e instanceof ImportException) {
                    throw (ImportException) e;
                } else {
                    throw ExceptionUtil.wrap2Runtime(e);
                }
            }
        });
        AsyncPageReadListener asyncReadListener = new AsyncPageReadListener(consumer,support, ctx,
            param.getBatchSize());
        ExcelReader reader = EasyExcel
            .read(param.getStream(), param.getModel(), asyncReadListener).build();
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        
        executor.execute(() -> {
            try {
                handler.init();
                support.beforeImport();
                reader.read(readSheet);
                support.onComplete(ctx);
            } catch (Exception e) {
                log.error("导入发生异常", e);
                if (e instanceof ImportException) {
                    ctx.setFailMessage(e.getMessage());
                } else {
                    ctx.setFailMessage("系统异常，联系管理员");
                }
                support.onError(ctx);
            }
        });
    }
}
