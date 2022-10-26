package com.asyncexcel.core.exporter;

import com.asyncexcel.core.ExceptionUtil;
import com.asyncexcel.core.ExportPage;
import com.asyncexcel.core.TriFunction;
import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/11 10:14
 */
public class AsyncExcelExporter {
    
    private final static Logger log = LoggerFactory.getLogger(AsyncExcelExporter.class);
    ExecutorService executor;
    
    public AsyncExcelExporter(ExecutorService executor) {
        this.executor = executor;
    }
    
    @Deprecated
    public void exportData(ExportHandler handler, ExportSupport support, DataExportParam param,
        ExportContext ctx) {
        
        BiFunction<Integer, Integer, ExportPage> dataFunction = (start, limit) -> {
            support.onExport(ctx);
            try {
                handler.beforePerPage(ctx, param);
                ExportPage exportPage = handler.exportData(start, limit, param);
                if (exportPage == null) {
                    throw new RuntimeException("导出数据为空");
                }
                if (CollectionUtils.isEmpty(exportPage.getRecords())) {
                    return exportPage;
                }
                ctx.record(exportPage.getRecords().size());
                support.onWrite(exportPage.getRecords(), ctx);
                handler.afterPerPage(exportPage.getRecords(), ctx, param);
                return exportPage;
            } catch (Exception e) {
                log.error("导出过程发生异常");
                if (e instanceof ExportException) {
                    throw (ExportException) e;
                } else {
                    throw ExceptionUtil.wrap2Runtime(e);
                }
            }
        };
        executor.execute(() -> {
            try {
                handler.init(ctx, param);
                int cursor = 1;
                ExportPage page = dataFunction.apply(cursor, param.getLimit());
                Long total = page.getTotal();
                ctx.getTask().setEstimateCount(total);
                long pageNum = (total + page.getSize() - 1) / page.getSize();
                for (cursor++; cursor <= pageNum; cursor++) {
                    dataFunction.apply(cursor, param.getLimit());
                }
                support.onComplete(ctx);
            } catch (Exception e) {
                log.error("导出异常", e);
                if (e instanceof ExportException) {
                    ctx.setFailMessage(e.getMessage());
                } else {
                    ctx.setFailMessage("系统异常，联系管理员");
                }
                support.onError(ctx);
            } finally {
                handler.callBack(ctx, param);
            }
        });
    }
    
    /**
     * 支持多sheet导出，支持按批次进行单元格合并等功能
     *
     * @param handlers
     * @param support
     * @param param
     * @param ctx
     */
    public void exportData(ExportSupport support, DataExportParam param, ExportContext ctx,
        ExportHandler... handlers) {
        TriFunction<ExportHandler, Integer, Integer, ExportPage> dataFunction = (h, start, limit) -> {
            support.onExport(ctx);
            try {
                h.beforePerPage(ctx, param);
                ExportPage exportPage = h.exportData(start, limit, param);
                if (CollectionUtils.isEmpty(exportPage.getRecords())) {
                    return exportPage;
                }
                ctx.record(exportPage.getRecords().size());
                support.onWrite(exportPage.getRecords(), ctx);
                h.afterPerPage(exportPage.getRecords(), ctx, param);
                return exportPage;
            } catch (Exception e) {
                log.error("导出过程发生异常");
                if (e instanceof ExportException) {
                    throw (ExportException) e;
                } else {
                    throw ExceptionUtil.wrap2Runtime(e);
                }
            }
        };
        
        executor.execute(() -> {
            try {
                if (handlers == null || handlers.length == 0) {
                    throw new ExportException("未设置导出处理类");
                }
                int sheetNo = 0;
                for (ExportHandler handler : handlers) {
                    handler.init(ctx, param);
                    if (ctx.getWriteSheet() != null) {
                        ctx.getWriteSheet().setSheetNo(sheetNo);
                    }
                    sheetNo++;
                    int cursor = 1;
                    ExportPage page = dataFunction.apply(handler, cursor, ctx.getLimit());
                    Long total = page.getTotal();
                    ctx.getTask().setEstimateCount(total + ctx.getTask().getEstimateCount());
                    Long pageNum = (total + page.getSize() - 1) / page.getSize();
                    for (cursor++; cursor <= pageNum; cursor++) {
                        dataFunction.apply(handler, cursor, ctx.getLimit());
                    }
                }
                support.onComplete(ctx);
            } catch (Exception e) {
                log.error("导出异常", e);
                if (e instanceof ExportException) {
                    ctx.setFailMessage(e.getMessage());
                } else {
                    ctx.setFailMessage("系统异常，联系管理员");
                }
                support.onError(ctx);
            } finally {
                for (ExportHandler handler : handlers) {
                    handler.callBack(ctx, param);
                }
            }
        });
    }
}
