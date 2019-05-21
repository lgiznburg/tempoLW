package ru.rsmu.tempoLW.consumabales;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelStyles {
    //standard font
    public XSSFFont getDefaultFont(XSSFWorkbook workbook) {
        XSSFFont defaultFont = workbook.createFont();
        defaultFont.setFontHeightInPoints((short) 10);
        defaultFont.setFontName("Times");
        defaultFont.setColor(IndexedColors.BLACK.getIndex());
        defaultFont.setBold(false);
        defaultFont.setItalic(false);
        return defaultFont;
    }

    //bold font
    public XSSFFont getBoldFont(XSSFWorkbook workbook) {
        XSSFFont bold = workbook.createFont();
        bold.setFontHeightInPoints((short) 10);
        bold.setBold(true);
        bold.setFontName("Times");
        bold.setColor(IndexedColors.BLACK.getIndex());
        bold.setItalic(false);
        return bold;
    }

    //style for standard value cells
    public CellStyle getBodyStyle (XSSFWorkbook workbook) {
        CellStyle body = workbook.createCellStyle();
        body.setWrapText(true);
        body.setFont(getDefaultFont(workbook));
        body.setBorderBottom(BorderStyle.THIN);
        body.setBorderTop(BorderStyle.THIN);
        body.setBorderLeft(BorderStyle.THIN);
        body.setBorderRight(BorderStyle.THIN);
        body.setAlignment(HorizontalAlignment.LEFT);
        return body;
    }

    //style for table headers
    public CellStyle getHeaderStyle (XSSFWorkbook workbook){
        CellStyle header = workbook.createCellStyle();
        header.cloneStyleFrom(getBodyStyle(workbook));
        header.setFont(getBoldFont(workbook));
        return header;
    }

    //style for bold texts (without borders)
    public CellStyle getPropertyNameStyle (XSSFWorkbook workbook){
        CellStyle propertyName = workbook.createCellStyle();
        propertyName.cloneStyleFrom(getHeaderStyle(workbook));
        propertyName.setBorderBottom(BorderStyle.NONE);
        propertyName.setBorderTop(BorderStyle.NONE);
        propertyName.setBorderLeft(BorderStyle.NONE);
        propertyName.setBorderRight(BorderStyle.NONE);
        propertyName.setAlignment(HorizontalAlignment.RIGHT);
        return  propertyName;
    }

    //style for standerd texts (without borders)
    public CellStyle getPropertyValueStyle(XSSFWorkbook workbook){
        CellStyle propertyValue = workbook.createCellStyle();
        propertyValue.cloneStyleFrom(getPropertyNameStyle(workbook));
        propertyValue.setFont(getDefaultFont(workbook));
        return propertyValue;
    }
}
