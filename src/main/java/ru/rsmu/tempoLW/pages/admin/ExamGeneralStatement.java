package ru.rsmu.tempoLW.pages.admin;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.consumabales.AttachmentRtf;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.TesteeDao;
import ru.rsmu.tempoLW.encoders.FileNameTransliterator;
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

    public StreamResponse onActivate() {
        List<Testee> testees = exam.getTestees();
        FileNameTransliterator trans = new FileNameTransliterator();
        String filename = "exam-" + trans.transliterateRuEn(exam.getName()).replaceAll("\\s", "_") + "-" + new SimpleDateFormat("dd_MM_yyyy").format(exam.getExamDate()) + "-statement.xls";

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(trans.transliterateRuEn(exam.getName()).replaceAll("\\s", "_"));

        //result table entry map
        Map<String, Object[]> data = new TreeMap<>();
        data.put("1", new Object[]{"№ личного дела", "ФИО", "результат"});
        if (testees.size() != 0) {
            int ord = 2;
            for (Testee testee : testees) {
                ExamResult result = examDao.findExamResultForTestee(exam, testee);
                data.put(Integer.toString(ord), new Object[]{testee.getCaseNumber(), testee.getLastName(), result == null ? "н/я" : result.getMarkTotal() == 0 ? "не завершено" : Integer.toString(result.getMarkTotal())});
                ord++;
            }
        }
        Set<String> keyset = data.keySet();

        //standard font
        HSSFFont defaultFont = workbook.createFont();
        defaultFont.setFontHeightInPoints((short)10);
        defaultFont.setFontName("Times");
        defaultFont.setColor(IndexedColors.BLACK.getIndex());
        defaultFont.setBold(false);
        defaultFont.setItalic(false);

        //bold font
        HSSFFont bold = workbook.createFont();
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
        for (String key : keyset) {
            Row row = sheet.createRow(rownum++);
            Object[] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr) {
                Cell cell = row.createCell(cellnum++);
                if(rownum == 5) {
                    cell.setCellStyle(header);
                } else { cell.setCellStyle(body); }
                if (obj instanceof String)
                    cell.setCellValue((String) obj);
                else if (obj instanceof Integer)
                    cell.setCellValue((Integer) obj);
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
            System.err.println(ie);
        }

        return new AttachmentRtf(document.toByteArray(), filename);
    }
}
