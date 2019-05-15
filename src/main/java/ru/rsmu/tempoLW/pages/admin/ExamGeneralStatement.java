package ru.rsmu.tempoLW.pages.admin;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import ru.rsmu.tempoLW.consumabales.AttachmentExcel;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.TesteeDao;
import ru.rsmu.tempoLW.consumabales.FileNameTransliterator;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.Testee;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExamGeneralStatement {

    @PageActivationContext
    private ExamSchedule exam;

    @Inject
    private ExamDao examDao;

    @Inject
    private TesteeDao testeeDao;

    @Inject
    Logger logger;

    public StreamResponse onActivate() {
        String filename = "exam-" + FileNameTransliterator.transliterateRuEn(exam.getName()).replaceAll("\\s", "_") + "-" + new SimpleDateFormat("dd_MM_yyyy").format(exam.getExamDate()) + "-statement.xls";

        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet(FileNameTransliterator.transliterateRuEn(exam.getName()).replaceAll("\\s", "_"));


        //standard font
        Font defaultFont = workbook.createFont();
        defaultFont.setFontHeightInPoints((short)10);
        defaultFont.setFontName("Times");
        defaultFont.setColor(IndexedColors.BLACK.getIndex());
        defaultFont.setBold(false);
        defaultFont.setItalic(false);

        //bold font
        Font bold = workbook.createFont();
        bold.setFontHeightInPoints((short)10);
        bold.setBold(true);
        bold.setFontName("Times");
        bold.setColor(IndexedColors.BLACK.getIndex());
        bold.setItalic(false);

        //style for standard value cells
        CellStyle body = workbook.createCellStyle();
        body.setWrapText(true);
        body.setFont(defaultFont);
        body.setBorderBottom(BorderStyle.THIN);
        body.setBorderTop(BorderStyle.THIN);
        body.setBorderLeft(BorderStyle.THIN);
        body.setBorderRight(BorderStyle.THIN);

        //style for table headers
        CellStyle header = workbook.createCellStyle();
        header.cloneStyleFrom(body);
        header.setFont(bold);

        //style for bold texts (without borders)
        CellStyle propertyName = workbook.createCellStyle();
        propertyName.cloneStyleFrom(header);
        propertyName.setBorderBottom(BorderStyle.NONE);
        propertyName.setBorderTop(BorderStyle.NONE);
        propertyName.setBorderLeft(BorderStyle.NONE);
        propertyName.setBorderRight(BorderStyle.NONE);

        //style for standerd texts (without borders)
        CellStyle propertyValue = workbook.createCellStyle();
        propertyValue.cloneStyleFrom(propertyName);
        propertyValue.setFont(defaultFont);

        //row with the title of statement
        Row rowTitle = sheet.createRow(0);
        Cell cellHead = rowTitle.createCell(1);
        cellHead.setCellStyle(propertyName);
        cellHead.setCellValue("Результаты экзамена абитуриентов");

        //row with the name of exam
        Row rowExamName = sheet.createRow(1);
        Cell cellExamNameTitle = rowExamName.createCell(0);
        cellExamNameTitle.setCellStyle(propertyName);
        cellExamNameTitle.setCellValue("Название экзамена:");
        Cell cellExamNameValue = rowExamName.createCell(1);
        cellExamNameValue.setCellStyle(propertyValue);
        cellExamNameValue.setCellValue(exam.getName());

        //row with the date of exam
        Row rowExamDate = sheet.createRow(2);
        Cell cellExamDateTitle = rowExamDate.createCell(0);
        cellExamDateTitle.setCellStyle(propertyName);
        cellExamDateTitle.setCellValue("Дата экзамена:");
        Cell cellExamDateValue = rowExamDate.createCell(1);
        cellExamDateValue.setCellStyle(propertyValue);
        cellExamDateValue.setCellValue(new SimpleDateFormat("dd.MM.yyyy").format(exam.getExamDate()));

        //empty row
        Row rowEmpty = sheet.createRow(3);
        Cell cellEmpty = rowEmpty.createCell(0);
        cellEmpty.setCellType(CellType.BLANK);

        //fill result table

        int rownum = 4;
        //result table header
        Row row = sheet.createRow(rownum++);
        Cell cell = row.createCell(0);
        cell.setCellStyle( header );
        cell.setCellValue( "№ личного дела" );

        cell = row.createCell(1);
        cell.setCellStyle( header );
        cell.setCellValue( "ФИО" );

        cell = row.createCell(2);
        cell.setCellStyle( header );
        cell.setCellValue( "Результат" );

        List<ExamResult> results = examDao.findExamResults( exam );

        if (exam.getTestees().size() != 0) {
            exam.getTestees().sort( new Comparator<Testee>() {
                @Override
                public int compare( Testee o1, Testee o2 ) {
                    return o1.getLastName().compareTo( o2.getLastName() );
                }
            } );
            // print results
            for (Testee testee : exam.getTestees() ) {
                row = sheet.createRow( rownum++ );
                cell = row.createCell(0);
                cell.setCellStyle( body );
                cell.setCellValue( testee.getCaseNumber() );

                cell = row.createCell(1);
                cell.setCellStyle( body );
                cell.setCellValue( testee.getLastName() );

                ExamResult result = findResultForTestee( testee, results );
                cell = row.createCell(2);
                cell.setCellStyle( body );
                if ( result == null ) {
                    cell.setCellValue( "н/я" );
                } else if ( result.isFinished() ){
                    cell.setCellValue( result.getMarkTotal() );
                } else {
                    cell.setCellValue( "не завершено" );
                }
            }
        }

        //set column width to match the width of value - there is no other way to affect the column width in POI
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);

        //convert table to bytearray and return
        ByteArrayOutputStream document = new ByteArrayOutputStream();
        try {
            workbook.write(document);
        } catch (IOException ie) {
            logger.error( "Can't create document", ie );
        }

        return new AttachmentExcel(document.toByteArray(), filename);
    }

    private ExamResult findResultForTestee( Testee testee, List<ExamResult> results ) {
        for ( ExamResult result : results ) {
            if ( result.getTestee().getCaseNumber().equals( testee.getCaseNumber() ) ) {
                return result;
            }
        }
        return null;
    }
}
