package ru.rsmu.tempoLW.consumabales;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelStyles {
    private final XSSFWorkbook workbook;
    private XSSFFont defaultFont;
    private XSSFFont boldFont;
    private CellStyle bodyStyle;
    private CellStyle headerStyle;
    private CellStyle propertyNameStyle;
    private CellStyle propertyValueStyle;

    public ExcelStyles( XSSFWorkbook workbook ) {
        this.workbook = workbook;
    }

    public XSSFFont getDefaultFont() {
        if ( defaultFont == null ) {
            defaultFont = createDefaultFont();
        }
        return defaultFont;
    }

    public XSSFFont getBoldFont() {
        if ( boldFont == null ) {
            boldFont = createBoldFont();
        }
        return boldFont;
    }

    public CellStyle getBodyStyle() {
        if ( bodyStyle == null ) {
            bodyStyle = createBodyStyle();
        }
        return bodyStyle;
    }

    public CellStyle getHeaderStyle() {
        if ( headerStyle == null ) {
            headerStyle = createHeaderStyle();
        }
        return headerStyle;
    }

    public CellStyle getPropertyNameStyle() {
        if ( propertyNameStyle == null ) {
            propertyNameStyle = createPropertyNameStyle();
        }
        return propertyNameStyle;
    }

    public CellStyle getPropertyValueStyle() {
        if ( propertyValueStyle == null ) {
            propertyValueStyle = createPropertyValueStyle();
        }
        return propertyValueStyle;
    }

    //standard font
    private XSSFFont createDefaultFont() {
        XSSFFont defaultFont = workbook.createFont();
        defaultFont.setFontHeightInPoints((short) 10);
        defaultFont.setFontName("Times");
        defaultFont.setColor(IndexedColors.BLACK.getIndex());
        defaultFont.setBold(false);
        defaultFont.setItalic(false);
        return defaultFont;
    }

    //bold font
    private XSSFFont createBoldFont() {
        XSSFFont bold = workbook.createFont();
        bold.setFontHeightInPoints((short) 10);
        bold.setBold(true);
        bold.setFontName("Times");
        bold.setColor(IndexedColors.BLACK.getIndex());
        bold.setItalic(false);
        return bold;
    }

    //style for standard value cells
    private CellStyle createBodyStyle() {
        CellStyle body = workbook.createCellStyle();
        body.setWrapText(true);
        body.setFont(getDefaultFont());
        body.setBorderBottom(BorderStyle.THIN);
        body.setBorderTop(BorderStyle.THIN);
        body.setBorderLeft(BorderStyle.THIN);
        body.setBorderRight(BorderStyle.THIN);
        body.setAlignment(HorizontalAlignment.LEFT);
        return body;
    }

    //style for table headers
    private CellStyle createHeaderStyle(){
        CellStyle header = workbook.createCellStyle();
        header.cloneStyleFrom(getBodyStyle());
        header.setFont(getBoldFont());
        return header;
    }

    //style for bold texts (without borders)
    private CellStyle createPropertyNameStyle(){
        CellStyle propertyName = workbook.createCellStyle();
        propertyName.cloneStyleFrom(getHeaderStyle());
        propertyName.setBorderBottom(BorderStyle.NONE);
        propertyName.setBorderTop(BorderStyle.NONE);
        propertyName.setBorderLeft(BorderStyle.NONE);
        propertyName.setBorderRight(BorderStyle.NONE);
        propertyName.setAlignment(HorizontalAlignment.RIGHT);
        return  propertyName;
    }

    //style for standerd texts (without borders)
    private CellStyle createPropertyValueStyle(){
        CellStyle propertyValue = workbook.createCellStyle();
        propertyValue.cloneStyleFrom(getPropertyNameStyle());
        propertyValue.setFont(getDefaultFont());
        return propertyValue;
    }
}
