package ru.rsmu.tempoLW.pages.admin;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
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
import ru.rsmu.tempoLW.entities.ExamToTestee;
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
        String filename = "exam-" + FileNameTransliterator.transliterateRuEn(exam.getName()).replaceAll("\\s", "_") + "-" + new SimpleDateFormat("dd_MM_yyyy").format(exam.getExamDate()) + "-statement.xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(FileNameTransliterator.transliterateRuEn(exam.getName()).replaceAll("\\s", "_"));


        //standard font
        XSSFFont defaultFont = workbook.createFont();
        defaultFont.setFontHeightInPoints((short)10);
        defaultFont.setFontName("Times");
        defaultFont.setColor(IndexedColors.BLACK.getIndex());
        defaultFont.setBold(false);
        defaultFont.setItalic(false);

        //bold font
        XSSFFont bold = workbook.createFont();
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
        body.setAlignment(HorizontalAlignment.LEFT);

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

        //row with the header
        Row rowTitle = sheet.createRow(0);
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,3));
        Cell cellHead = CellUtil.createCell(rowTitle,0, "Российский национальный исследовательский медицинский университет им. Н.И. Пирогова Минздрава России");
        CellUtil.setAlignment(cellHead, HorizontalAlignment.CENTER);
        CellUtil.setFont(cellHead, bold);

        //row with the title of statement
        rowTitle = sheet.createRow(1);
        sheet.addMergedRegion(new CellRangeAddress(1,1,1,3));
        cellHead = CellUtil.createCell(rowTitle,0, "Результаты экзамена абитуриентов");
        CellUtil.setAlignment(cellHead, HorizontalAlignment.CENTER);
        CellUtil.setFont(cellHead, bold);
        propertyName.setAlignment(HorizontalAlignment.RIGHT);

        //row with the name of exam
/*
        Row rowExamName = sheet.createRow(2);
        Cell cellExamNameTitle = rowExamName.createCell(1);
        cellExamNameTitle.setCellStyle(propertyName);
        cellExamNameTitle.setCellValue("Название экзамена:");
        Cell cellExamNameValue = rowExamName.createCell(2);
        cellExamNameValue.setCellStyle(propertyValue);
        cellExamNameValue.setCellValue(exam.getName());
*/

        //row with subject
        Row rowExamSubject = sheet.createRow(2);
        Cell cellExamSubjectTitle = rowExamSubject.createCell(1);
        cellExamSubjectTitle.setCellStyle(propertyName);
        cellExamSubjectTitle.setCellValue("Предмет:");
        Cell cellExamSubjectValue = rowExamSubject.createCell(2);
        cellExamSubjectValue.setCellStyle(propertyValue);
        cellExamSubjectValue.setCellValue(exam.getTestingPlan().getSubject().getTitle());

        //row with language
/*
        Row rowExamLocale = sheet.createRow(3);
        Cell cellExamLocaleTitle = rowExamLocale.createCell(1);
        cellExamLocaleTitle.setCellStyle(propertyName);
        cellExamLocaleTitle.setCellValue("Язык:");
        Cell cellExamLocaleValue = rowExamLocale.createCell(2);
        cellExamLocaleValue.setCellStyle(propertyValue);
        cellExamLocaleValue.setCellValue(exam.getTestingPlan().getSubject().getLocale().equals("ru") ? "русский" : "английский");
*/

        //row with the date of exam
        Row rowExamDate = sheet.createRow(3);
        Cell cellExamDateTitle = rowExamDate.createCell(1);
        cellExamDateTitle.setCellStyle(propertyName);
        cellExamDateTitle.setCellValue("Дата экзамена:");
        Cell cellExamDateValue = rowExamDate.createCell(2);
        cellExamDateValue.setCellStyle(propertyValue);
        cellExamDateValue.setCellValue(new SimpleDateFormat("dd.MM.yyyy").format(exam.getExamDate()));

        //empty row
        Row rowEmpty = sheet.createRow(4);
        Cell cellEmpty = rowEmpty.createCell(0);
        cellEmpty.setCellType(CellType.BLANK);

        //fill result table

        int rownum = 5;
        //result table header
        Row row = sheet.createRow(rownum++);
        Cell cell = row.createCell(0);
        cell.setCellStyle( header );
        cell.setCellValue( "№ п/п" );

        cell = row.createCell(1);
        cell.setCellStyle( header );
        cell.setCellValue( "№ личного дела" );

        cell = row.createCell(2);
        cell.setCellStyle( header );
        cell.setCellValue( "ФИО" );

        cell = row.createCell(3);
        cell.setCellStyle( header );
        cell.setCellValue( "Результат" );

        List<ExamResult> results = examDao.findExamResults( exam );
        int num = 1;

        if (exam.getExamToTestees().size() != 0) {
            exam.getExamToTestees().sort( new Comparator<ExamToTestee>() {
                @Override
                public int compare( ExamToTestee o1, ExamToTestee o2 ) {
                    return o1.getTestee().getLastName().compareTo( o2.getTestee().getLastName() );
                }
            } );
            // print results
            for (ExamToTestee examToTestee : exam.getExamToTestees() ) {
                row = sheet.createRow( rownum++ );
                cell = row.createCell(0);
                cell.setCellStyle( body );
                cell.setCellValue( num );

                num++;

                cell = row.createCell(1);
                cell.setCellStyle( body );
                cell.setCellValue( examToTestee.getTestee().getCaseNumber() );

                cell = row.createCell(2);
                cell.setCellStyle( body );
                cell.setCellValue( examToTestee.getTestee().getLastName() );

                ExamResult result = findResultForTestee( examToTestee.getTestee(), results );
                cell = row.createCell(3);
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

        //empty row nr. 2
        Row rowEmpty1 = sheet.createRow(rownum++);
        Cell cellEmpty1 = rowEmpty1.createCell(0);
        cellEmpty1.setCellType(CellType.BLANK);

        //spaces for signatures of the Examination commission members
        for (int i = 0; i <= 2; i++) {
            Row rowExaminer = sheet.createRow(rownum++);
            sheet.addMergedRegion(new CellRangeAddress(rownum - 1,rownum - 1,0,3));
            Cell examinerSignature = CellUtil.createCell(rowExaminer, 0, "Член экзаменационной комиссии: _______________________ / ______________ /");
            CellUtil.setFont(examinerSignature, defaultFont);
            CellUtil.setAlignment(examinerSignature, HorizontalAlignment.CENTER);
        }

        //set column width to match the width of value - there is no other way to affect the column width in POI
        for (int i = 0; i <= 3; i++) {
            sheet.autoSizeColumn(i);
        }

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