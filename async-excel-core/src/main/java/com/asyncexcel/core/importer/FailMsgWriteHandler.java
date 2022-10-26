package com.asyncexcel.core.importer;

import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.style.AbstractCellStyleStrategy;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/10/26 10:02
 */
public class FailMsgWriteHandler extends AbstractCellStyleStrategy {
    
    
    @Override
    protected void setHeadCellStyle(CellWriteHandlerContext context) {
    }
    
    @Override
    protected void setContentCellStyle(CellWriteHandlerContext context) {
        String title=context.getHeadData().getHeadNameList().get(0);
        if (SheetConst.FAIL_MSG_TITLE.equals(title)||SheetConst.FAIL_ROW_TITLE.equals(title)){
            Workbook workbook = context.getCell().getSheet().getWorkbook();
            CellStyle cellStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 14);
            font.setBold(true);
            font.setColor(Font.COLOR_RED);
            cellStyle.setFont(font);
            context.getCell().setCellStyle(cellStyle);
        }
    }
}
