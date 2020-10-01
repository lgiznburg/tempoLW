package ru.rsmu.tempoLW.pages.admin;

import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfStreamSource;
import com.tutego.jrtf.*;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;
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
import ru.rsmu.rtf.FieldModifier;
import ru.rsmu.rtf.TableModifier;
import ru.rsmu.rtf.model.RtfDocument;
import ru.rsmu.rtf.parser.TemplateRtfListener;
import ru.rsmu.rtf.parser.TemplateRtfParser;
import ru.rsmu.tempoLW.consumabales.AttachmentExcel;
import ru.rsmu.tempoLW.consumabales.AttachmentRtf;
import ru.rsmu.tempoLW.consumabales.ExcelStyles;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.consumabales.FileNameTransliterator;
import ru.rsmu.tempoLW.dao.RtfTemplateDao;
import ru.rsmu.tempoLW.entities.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author leonid.
 */
public class ExamTesteeResult {
    @PageActivationContext(index = 0)
    private ExamSchedule exam;

    @PageActivationContext(index = 1)
    private Testee testee;

    @PageActivationContext(index = 2)
    private Boolean rtf;

    @Inject
    private ExamDao examDao;

    @Inject
    private RtfTemplateDao rtfTemplateDao;

    @Inject
    private Locale currentLocale;

    @Inject
    Logger logger;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");

    public StreamResponse onActivate() throws IOException {
        if(rtf == null) { rtf = false; }
        if(rtf) {
            DocumentTemplate template = rtfTemplateDao.findByType( DocumentTemplateType.EXAM_RESULT );
            if ( template == null ) {
                throw new RuntimeException("Rtf Template does not exist");
            }

            InputStream is = new ByteArrayInputStream( template.getRtfTemplate().getBytes() );
            IRtfSource source = new RtfStreamSource(is);
            IRtfParser parser = new TemplateRtfParser();
            TemplateRtfListener listener = new TemplateRtfListener();
            parser.parse(source, listener);

            RtfDocument doc = listener.getDocument();

            FieldModifier fm = new FieldModifier();
            TableModifier tm = new TableModifier();

            fm.put( "examSubject", StringUtils.join( exam.getTestingPlan().getSubject().getTitle(), " ( ",
                    exam.getTestingPlan().getName(), " )" ) );
            fm.put( "examDate", new SimpleDateFormat( "dd MMMM yyyy", currentLocale ).format( exam.getExamDate() ) );
            fm.put( "caseNumber",  testee.getCaseNumber() );
            fm.put( "testeeName", testee.getFullName() );

            String fileName = "unknown.rtf";

            if (exam != null && testee != null) {
                //forming file name with transliteration
                String examName = FileNameTransliterator.transliterateRuEn(exam.getTestingPlan().getSubject().getTitle());
                String examDate = sdf.format(exam.getExamDate());
                examName = examName.replaceAll("\\s", "_");
                fileName = testee.getCaseNumber() + "_" + examName + "_" + examDate + ".rtf";
                ExamResult result = examDao.findExamResultForTestee(exam, testee);
                int num = 1;
                List<List<String>> resultsTable = new LinkedList<>();
                if (result != null) {
                    fm.put( "finalMark", String.valueOf(result.getMarkTotal()) );
                    for (QuestionResult questionResult : result.getQuestionResults()) {
                        List<String> row = new LinkedList<>();
                        row.add( String.valueOf(num++) );
                        row.add( questionResult.getQuestion().getQuestionInfo().getName() );
                        row.add( StringUtils.join(
                                String.valueOf(questionResult.getMark()),
                                " ( из ",
                                String.valueOf( questionResult.getQuestion().getQuestionInfo().getMaxScore()*questionResult.getScoreCost() ),
                                " )"
                        ) );
                        resultsTable.add( row );
                    }
                }
                else {
                    fm.put( "finalMark", "" );
                }
                tm.put( "T1", resultsTable );
            }

            fm.modify( doc );
            tm.modify( doc );

            ByteArrayOutputStream document = new ByteArrayOutputStream();
            doc.output( document );

            return new AttachmentRtf(document.toByteArray(), fileName);
        }
        else {
            if (exam != null && testee != null) {
                String filename = testee.getCaseNumber() + "_" + FileNameTransliterator.transliterateRuEn(exam.getTestingPlan().getSubject().getTitle()).replaceAll("\\s", "_") + "-" + sdf.format(exam.getExamDate()) + "-result.xlsx";
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = workbook.createSheet(testee.getCaseNumber());
                ExcelStyles styles = new ExcelStyles( workbook );
                //row with the header
                Row rowTitle = sheet.createRow(0);
                sheet.addMergedRegion(new CellRangeAddress(0,0,0,2));
                Cell cellHead = CellUtil.createCell(rowTitle,0, "Российский национальный исследовательский медицинский университет им. Н.И. Пирогова Минздрава России");
                CellUtil.setAlignment(cellHead, HorizontalAlignment.CENTER);
                CellUtil.setFont(cellHead, styles.getBoldFont());

                //row w/ title
                rowTitle = sheet.createRow(1);
                sheet.addMergedRegion(new CellRangeAddress(1,1,0,2));
                cellHead = CellUtil.createCell(rowTitle,0, "Результаты вступительных испытаний абитуриента");
                CellUtil.setAlignment(cellHead, HorizontalAlignment.CENTER);
                CellUtil.setFont(cellHead, styles.getBoldFont());

                //row w/ testee name
                Row rowExamName = sheet.createRow(2);
                sheet.addMergedRegion(new CellRangeAddress(2,2,1,2));
                Cell cellExamNameTitle = rowExamName.createCell(0);
                cellExamNameTitle.setCellStyle( styles.getPropertyNameStyle());
                cellExamNameTitle.setCellValue("ФИО абитуриента:");
                Cell cellExamNameValue = CellUtil.createCell(rowExamName, 1, testee.getLastName());
                CellUtil.setFont(cellExamNameValue, styles.getDefaultFont());

                //row with subject
                Row rowExamSubject = sheet.createRow(3);
                sheet.addMergedRegion(new CellRangeAddress(3,3,1,2));
                Cell cellExamSubjectTitle = rowExamSubject.createCell(0);
                cellExamSubjectTitle.setCellStyle( styles.getPropertyNameStyle());
                cellExamSubjectTitle.setCellValue("Предмет:");
                Cell cellExamSubjectValue = CellUtil.createCell(rowExamSubject, 1, exam.getTestingPlan().getSubject().getTitle());
                CellUtil.setFont(cellExamSubjectValue, styles.getDefaultFont());

                //row with language
/*
                Row rowExamLocale = sheet.createRow(3);
                sheet.addMergedRegion(new CellRangeAddress(3,3,1,2));
                Cell cellExamLocaleTitle = rowExamLocale.createCell(0);
                cellExamLocaleTitle.setCellStyle( styles.getPropertyNameStyle());
                cellExamLocaleTitle.setCellValue("Язык:");
                Cell cellExamLocaleValue = CellUtil.createCell(rowExamLocale, 1, exam.getTestingPlan().getSubject().getLocale().equals("ru") ? "русский" : "английский");
                CellUtil.setFont(cellExamLocaleValue, styles.getDefaultFont());
*/

                //row with the date of exam
                Row rowExamDate = sheet.createRow(4);
                sheet.addMergedRegion(new CellRangeAddress(4,4,1,2));
                Cell cellExamDateTitle = rowExamDate.createCell(0);
                cellExamDateTitle.setCellStyle( styles.getPropertyNameStyle());
                cellExamDateTitle.setCellValue("Дата экзамена:");
                Cell cellExamDateValue = CellUtil.createCell(rowExamDate, 1, new SimpleDateFormat("dd.MM.yyyy").format(exam.getExamDate()));
                CellUtil.setFont(cellExamDateValue, styles.getDefaultFont());

                //empty row
                Row rowEmpty = sheet.createRow(5);
                Cell cellEmpty = rowEmpty.createCell(0);
                cellEmpty.setCellType(CellType.BLANK);

                int rownum = 6;
                //result table header
                Row row = sheet.createRow(rownum++);
                Cell cell = row.createCell(0);
                cell.setCellStyle( styles.getHeaderStyle() );
                cell.setCellValue( "Вопрос" );

                cell = row.createCell(1);
                cell.setCellStyle( styles.getHeaderStyle() );
                cell.setCellValue( "Базовый балл" );

                cell = row.createCell(2);
                cell.setCellStyle( styles.getHeaderStyle() );
                cell.setCellValue( "Итоговая оценка" );

                CellStyle markStyle = styles.getBodyStyle();

                int num = 1;

                ExamResult result = examDao.findExamResultForTestee(exam, testee);
                if(result != null) {
                    for (QuestionResult questionResult : result.getQuestionResults()) {
                        row = sheet.createRow(rownum++);
                        cell = row.createCell(0);
                        cell.setCellStyle( styles.getBodyStyle() );
                        cell.setCellValue(num + ". " + questionResult.getQuestion().getQuestionInfo().getName());

                        num++;

                        cell = row.createCell(1);
                        cell.setCellStyle( markStyle );
                        cell.setCellValue( questionResult.getScore() + " ( из " + questionResult.getQuestion().getQuestionInfo().getMaxScore() + " )" );

                        cell = row.createCell(2);
                        markStyle.setAlignment(HorizontalAlignment.CENTER);
                        cell.setCellStyle( markStyle );
                        cell.setCellValue( questionResult.getMark() );
                    }
                }

                Row finalMarkRow = sheet.createRow(rownum++);
                Cell finalMarkCell = CellUtil.createCell(finalMarkRow, 0, "Результирующая оценка");
                CellUtil.setAlignment(finalMarkCell, HorizontalAlignment.LEFT);
                CellUtil.setFont(finalMarkCell, styles.getBoldFont());

                //map with borders
                Map<String, Object> borders = new LinkedMap<>();
                borders.put(CellUtil.BORDER_BOTTOM, BorderStyle.THIN);
                borders.put(CellUtil.BORDER_TOP, BorderStyle.THIN);
                borders.put(CellUtil.BORDER_LEFT, BorderStyle.THIN);
                borders.put(CellUtil.BORDER_RIGHT, BorderStyle.THIN);

                CellUtil.setCellStyleProperties(finalMarkCell, borders);
                CellUtil.setCellStyleProperty(finalMarkCell, CellUtil.BORDER_RIGHT, BorderStyle.NONE);

                Cell finalMarkEmptyCell = CellUtil.createCell(finalMarkRow, 1, "");
                CellUtil.setAlignment(finalMarkCell, HorizontalAlignment.LEFT);
                CellUtil.setFont(finalMarkCell, styles.getBoldFont());

                CellUtil.setCellStyleProperties(finalMarkEmptyCell, borders);
                CellUtil.setCellStyleProperty(finalMarkEmptyCell, CellUtil.BORDER_RIGHT, BorderStyle.NONE);
                CellUtil.setCellStyleProperty(finalMarkEmptyCell, CellUtil.BORDER_LEFT, BorderStyle.NONE);

                Cell totalMarkCell = finalMarkRow.createCell(2);
                totalMarkCell.setCellStyle( markStyle );
                totalMarkCell.setCellValue( result.getMarkTotal() );
                CellUtil.setCellStyleProperty(totalMarkCell, CellUtil.BORDER_LEFT, BorderStyle.NONE);

                //empty row nr. 2
                Row rowEmpty1 = sheet.createRow(rownum++);
                Cell cellEmpty1 = rowEmpty1.createCell(0);
                cellEmpty1.setCellType(CellType.BLANK);

                Row acknowledgeRow = sheet.createRow(rownum++);
                sheet.addMergedRegion(new CellRangeAddress(rownum - 1,rownum - 1,0,2));
                Cell acknowledgeCell = CellUtil.createCell(acknowledgeRow, 0, "С результатами вступительного испытания ознакомлен.");
                CellUtil.setFont(acknowledgeCell, styles.getDefaultFont());
                CellUtil.setCellStyleProperty(acknowledgeCell, CellUtil.WRAP_TEXT, true);
                CellUtil.setAlignment(acknowledgeCell, HorizontalAlignment.LEFT);

                Row acknowledgeRow1 = sheet.createRow(rownum++);
                sheet.addMergedRegion(new CellRangeAddress(rownum - 1,rownum - 1,0,2));
                Cell acknowledgeCell1 = CellUtil.createCell(acknowledgeRow1, 0, "Жалоб на самочувствие в процессе вступительного испытания не возникло.");
                CellUtil.setFont(acknowledgeCell1, styles.getDefaultFont());
                CellUtil.setAlignment(acknowledgeCell1, HorizontalAlignment.LEFT);

                Row acknowledgeRow2 = sheet.createRow(rownum++);
                sheet.addMergedRegion(new CellRangeAddress(rownum - 1,rownum - 1,0,2));
                Cell acknowledgeCell2 = CellUtil.createCell(acknowledgeRow2, 0, "К процессу проведения вступительного испытания претензий не имею.");
                CellUtil.setFont(acknowledgeCell2, styles.getDefaultFont());
                CellUtil.setAlignment(acknowledgeCell2, HorizontalAlignment.LEFT);

                Row studentRow = sheet.createRow(rownum++);
                sheet.addMergedRegion(new CellRangeAddress(rownum - 1,rownum - 1,0,2));
                Cell studentCell = CellUtil.createCell(studentRow, 0, "Абитуриент: _______________________ / " + testee.getLastName() + " /");
                CellUtil.setFont(studentCell, styles.getDefaultFont());
                CellUtil.setAlignment(studentCell, HorizontalAlignment.RIGHT);

                //spaces for signatures of the Examination commission members
                for (int i = 0; i <= 2; i++) {
                    Row rowExaminer = sheet.createRow(rownum++);
                    sheet.addMergedRegion(new CellRangeAddress(rownum - 1,rownum - 1,0,2));
                    Cell examinerSignature = CellUtil.createCell(rowExaminer, 0, "Член экзаменационной комиссии: _______________________ / ______________ /");
                    CellUtil.setFont(examinerSignature, styles.getDefaultFont());
                    CellUtil.setAlignment(examinerSignature, HorizontalAlignment.RIGHT);
                }

                //set column width to match the width of value - there is no other way to affect the column width in POI
                for (int i = 0; i < 3; i++) {
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
        }

        return null;
    }
}
