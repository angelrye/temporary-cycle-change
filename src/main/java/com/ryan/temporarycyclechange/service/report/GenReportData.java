package com.ryan.temporarycyclechange.service.report;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * 
 * @author jchin13
 */
public class GenReportData {

    public Workbook generateData(Object[][] dataInput) throws IOException, InvalidFormatException { 

        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet("TempCycleChange");
        
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderTop(CellStyle.BORDER_MEDIUM);
        cellStyle.setBorderBottom(CellStyle.BORDER_MEDIUM);
        cellStyle.setBorderLeft(CellStyle.BORDER_MEDIUM);
        cellStyle.setBorderRight(CellStyle.BORDER_MEDIUM);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);

        for (int x = 0; x <= dataInput.length - 1; x++) {
            Row row = sheet.createRow(x);
            for (int y = 0; y <= 5; y++) {
                //display data
                System.out.println(dataInput[x][y]);
                Cell cell = row.createCell(y);
                cell.setCellStyle(cellStyle);

                if (dataInput[x][y] instanceof String) {
                    cell.setCellValue((String) dataInput[x][y]);
                } else if (dataInput[x][y] instanceof Integer) {
                    cell.setCellValue((Integer) dataInput[x][y]);
                };
            }
        }
        
        return workbook;
    }
};
    