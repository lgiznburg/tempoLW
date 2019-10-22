package ru.rsmu.tempoLW.pages.admin;

import com.tutego.jrtf.*;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import ru.rsmu.tempoLW.consumabales.AttachmentExcel;
import ru.rsmu.tempoLW.consumabales.AttachmentRtf;
import ru.rsmu.tempoLW.consumabales.ExcelStyles;
import ru.rsmu.tempoLW.consumabales.FileNameTransliterator;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.dao.internal.QuestionDaoImpl;
import ru.rsmu.tempoLW.data.QuestionType;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.pages.Index;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class AppealStatement {
    @PageActivationContext(index = 0)
    private ExamSchedule exam;

    @PageActivationContext(index = 1)
    private Testee testee;

    @PageActivationContext(index = 2)
    private Boolean rtf;

    @Inject
    private ExamDao examDao;

    @Inject
    private Locale currentLocale;

    @Inject
    Logger logger;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");

    public StreamResponse onActivate() {
        if (exam != null && testee != null) {
            String filename = testee.getCaseNumber() + "_" + FileNameTransliterator.transliterateRuEn(exam.getTestingPlan().getSubject().getTitle()).replaceAll("\\s", "_") + "-" + sdf.format(exam.getExamDate()) + "-appeal.xlsx";
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(testee.getCaseNumber());
            ExcelStyles styles = new ExcelStyles( workbook );

            //map with borders
            Map<String, Object> borders = new LinkedMap<>();
            borders.put(CellUtil.BORDER_BOTTOM, BorderStyle.THIN);
            borders.put(CellUtil.BORDER_TOP, BorderStyle.THIN);
            borders.put(CellUtil.BORDER_LEFT, BorderStyle.THIN);
            borders.put(CellUtil.BORDER_RIGHT, BorderStyle.THIN);

            //row w/ title
            Row rowTitle = sheet.createRow(0);
            sheet.addMergedRegion(new CellRangeAddress(0,0,0,2));
            Cell cellHead = CellUtil.createCell(rowTitle,0, "Аппеляционная ведомость абитуриента");
            CellUtil.setAlignment(cellHead, HorizontalAlignment.CENTER);
            CellUtil.setFont(cellHead, styles.getBoldFont());

            //row w/ testee name
            Row rowExamName = sheet.createRow(1);
            //sheet.addMergedRegion(new CellRangeAddress(1,1,1,1));
            Cell cellExamNameTitle = rowExamName.createCell(0);
            cellExamNameTitle.setCellStyle( styles.getPropertyNameStyle());
            cellExamNameTitle.setCellValue("ФИО абитуриента:");
            Cell cellExamNameValue = CellUtil.createCell(rowExamName, 2, testee.getLastName());
            CellUtil.setFont(cellExamNameValue, styles.getDefaultFont());

            //row with subject
            Row rowExamSubject = sheet.createRow(2);
            Cell cellExamSubjectTitle = rowExamSubject.createCell(0);
            cellExamSubjectTitle.setCellStyle( styles.getPropertyNameStyle());
            cellExamSubjectTitle.setCellValue("Предмет:");
            Cell cellExamSubjectValue = CellUtil.createCell(rowExamSubject, 2, exam.getTestingPlan().getSubject().getTitle());
            CellUtil.setFont(cellExamSubjectValue, styles.getDefaultFont());

            //row with language
            Row rowExamLocale = sheet.createRow(3);
            //sheet.addMergedRegion(new CellRangeAddress(3,3,1,2));
            Cell cellExamLocaleTitle = rowExamLocale.createCell(0);
            cellExamLocaleTitle.setCellStyle( styles.getPropertyNameStyle());
            cellExamLocaleTitle.setCellValue("Язык:");
            Cell cellExamLocaleValue = CellUtil.createCell(rowExamLocale, 2, exam.getTestingPlan().getSubject().getLocale().equals("ru") ? "русский" : "английский");
            CellUtil.setFont(cellExamLocaleValue, styles.getDefaultFont());

            //row with the date of exam
            Row rowExamDate = sheet.createRow(4);
            //sheet.addMergedRegion(new CellRangeAddress(4,4,1,2));
            Cell cellExamDateTitle = rowExamDate.createCell(0);
            cellExamDateTitle.setCellStyle( styles.getPropertyNameStyle());
            cellExamDateTitle.setCellValue("Дата экзамена:");
            Cell cellExamDateValue = CellUtil.createCell(rowExamDate, 2, new SimpleDateFormat("dd.MM.yyyy").format(exam.getExamDate()));
            CellUtil.setFont(cellExamDateValue, styles.getDefaultFont());

            //row with the date of фззуфд
            Row rowAppealDate = sheet.createRow(5);
            //sheet.addMergedRegion(new CellRangeAddress(5,5,1,2));
            Cell cellAppealDateTitle = rowAppealDate.createCell(0);
            cellAppealDateTitle.setCellStyle( styles.getPropertyNameStyle());
            cellAppealDateTitle.setCellValue("Дата апелляции:");
            Cell cellAppealDateValue = CellUtil.createCell(rowAppealDate, 2, new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
            CellUtil.setFont(cellAppealDateValue, styles.getDefaultFont());

            //empty row
            Row rowEmpty = sheet.createRow(6);
            Cell cellEmpty = rowEmpty.createCell(0);
            cellEmpty.setCellType(CellType.BLANK);

            int rownum = 7;
            //result table header
            Row row = sheet.createRow(rownum++);
            Cell cell = row.createCell(0);
            cell.setCellStyle( styles.getHeaderStyle() );
            cell.setCellValue( "№ и тема" );

            cell = row.createCell(1);
            cell.setCellStyle( styles.getHeaderStyle() );
            cell.setCellValue( "Правильность" );

            cell = row.createCell(2);
            cell.setCellStyle( styles.getHeaderStyle() );
            cell.setCellValue( "Вопрос" );

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

                    row = sheet.createRow(rownum++);
                    Cell textTitleCell = row.createCell(0);
                    textTitleCell.setCellStyle( markStyle );
                    textTitleCell.setCellValue( "Текст задания:" );
                    CellUtil.setCellStyleProperty(textTitleCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                    CellUtil.setFont(textTitleCell, styles.getBoldFont());
                    //sheet.addMergedRegion(new CellRangeAddress(rownum-1,rownum-1,1,2));
                    Cell textValueCell = CellUtil.createCell(row, 2, questionResult.getQuestion().getText());
                    CellUtil.setCellStyleProperties(textValueCell, borders);

                    /*
                     * for simple questions
                     */
                    if(questionResult.getQuestion().getType().equals(QuestionType.SIMPLE)) {
                        QuestionSimple question = (QuestionSimple) questionResult.getQuestion();
                        List<AnswerVariant> answerVariants = question.getAnswerVariants();
                        List<ResultElement> resultElements = questionResult.getElements();
                        List<ResultSimple> resultsSimple = new ArrayList<>();

                        if(resultElements != null) {
                            for (ResultElement element : resultElements) {
                                ResultSimple simple = (ResultSimple) element;
                                resultsSimple.add(simple);
                            }
                        }

                        if (answerVariants != null) {
                            for (AnswerVariant answerVariant : answerVariants) {
                                if (answerVariant.isCorrect()) {
                                    row = sheet.createRow(rownum++);
                                    Cell correctTitleCell = row.createCell(0);
                                    correctTitleCell.setCellStyle(markStyle);
                                    correctTitleCell.setCellValue("Верный ответ:");
                                    CellUtil.setCellStyleProperty(correctTitleCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                                    CellUtil.setFont(correctTitleCell, styles.getBoldFont());
                                    cell = row.createCell(1);
                                    cell.setCellStyle( styles.getBodyStyle());
                                    cell.setCellType(CellType.BLANK);
                                    cell = row.createCell(2);
                                    cell.setCellStyle( styles.getBodyStyle());
                                    cell.setCellValue(answerVariant.getText());
                                }
                            }
                        }


                        if(resultsSimple != null) {
                            for (ResultSimple resultSimple : resultsSimple) {
                                row = sheet.createRow(rownum++);
                                Cell chosenTitleCell = row.createCell(0);
                                chosenTitleCell.setCellStyle(markStyle);
                                chosenTitleCell.setCellValue("Выбранный ответ:");
                                CellUtil.setCellStyleProperty(chosenTitleCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                                CellUtil.setFont(chosenTitleCell, styles.getBoldFont());
                                cell = row.createCell(1);
                                cell.setCellStyle( styles.getBodyStyle());
                                cell.setCellValue(resultSimple.isCorrect() ? "верно" : "неверно");
                                cell = row.createCell(2);
                                cell.setCellStyle( styles.getBodyStyle());
                                cell.setCellValue(resultSimple.getAnswerVariant().getText());
                            }
                        } else {
                            row = sheet.createRow(rownum++);
                            Cell chosenTitleCell = row.createCell(0);
                            chosenTitleCell.setCellStyle(markStyle);
                            chosenTitleCell.setCellValue("Выбранный ответ:");
                            CellUtil.setCellStyleProperty(chosenTitleCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                            CellUtil.setFont(chosenTitleCell, styles.getBoldFont());
                            cell = row.createCell(1);
                            cell.setCellStyle( styles.getBodyStyle());
                            cell.setCellValue("неверно");
                            cell = row.createCell(2);
                            cell.setCellStyle( styles.getBodyStyle());
                            cell.setCellValue("Не выбран");
                        }
                    }

                    /*
                     * for open questions
                     */
                    if(questionResult.getQuestion().getType().equals(QuestionType.OPEN)) {
                        QuestionOpen question = (QuestionOpen) questionResult.getQuestion();
                        List<AnswerVariant> answerVariants = question.getAnswerVariants();
                        List<ResultElement> resultElements = questionResult.getElements();
                        List<ResultOpen> resultsOpen = new ArrayList<>();

                        if(resultElements != null) {
                            for (ResultElement element : resultElements) {
                                ResultOpen open = (ResultOpen) element;
                                resultsOpen.add(open);
                            }
                        }

                        if (answerVariants != null) {
                            for (AnswerVariant answerVariant : answerVariants) {
                                //if (answerVariant.isCorrect()) {  // for Open question each answer variant is correct
                                    row = sheet.createRow(rownum++);
                                    Cell correctTitleCell = row.createCell(0);
                                    correctTitleCell.setCellStyle(markStyle);
                                    correctTitleCell.setCellValue("Верный ответ:");
                                    CellUtil.setCellStyleProperty(correctTitleCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                                    CellUtil.setFont(correctTitleCell, styles.getBoldFont());
                                    cell = row.createCell(1);
                                    cell.setCellType(CellType.BLANK);
                                    cell = row.createCell(2);
                                    cell.setCellStyle( styles.getBodyStyle());
                                    cell.setCellValue(answerVariant.getReadableText());
                                //}
                            }
                        }


                        if(resultsOpen != null) {
                            for (ResultOpen resultOpen : resultsOpen) {
                                row = sheet.createRow(rownum++);
                                Cell chosenTitleCell = row.createCell(0);
                                chosenTitleCell.setCellStyle(markStyle);
                                chosenTitleCell.setCellValue("Введённый ответ:");
                                CellUtil.setCellStyleProperty(chosenTitleCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                                CellUtil.setFont(chosenTitleCell, styles.getBoldFont());
                                cell = row.createCell(1);
                                cell.setCellStyle( styles.getBodyStyle());
                                List<ResultElement> results = questionResult.getElements();
                                cell.setCellValue(resultOpen.getQuestionResult().getQuestion().countErrors(results) == 0 ? "верно" : "неверно");
                                cell = row.createCell(2);
                                cell.setCellStyle( styles.getBodyStyle());
                                cell.setCellValue(resultOpen.getValue());
                            }
                        } else {
                            row = sheet.createRow(rownum++);
                            Cell chosenTitleCell = row.createCell(0);
                            chosenTitleCell.setCellStyle(markStyle);
                            chosenTitleCell.setCellValue("Введённый ответ:");
                            CellUtil.setCellStyleProperty(chosenTitleCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                            CellUtil.setFont(chosenTitleCell, styles.getBoldFont());
                            cell = row.createCell(1);
                            cell.setCellStyle( styles.getBodyStyle());
                            cell.setCellValue("неверно");
                            cell = row.createCell(2);
                            cell.setCellStyle( styles.getBodyStyle());
                            cell.setCellValue("Не введён");
                        }
                    }

                    /*
                     * for correspondence questions
                     */
                    if(questionResult.getQuestion().getType().equals(QuestionType.CORRESPONDENCE)) {
                        QuestionCorrespondence question = (QuestionCorrespondence) questionResult.getQuestion();
                        List<CorrespondenceVariant> allVariants = question.getCorrespondenceVariants();
                        List<ResultElement> resultElements = questionResult.getElements();
                        List<ResultCorrespondence> resultsCorrespondence = new ArrayList<>();

                        if(resultElements != null) {
                            for (ResultElement element : resultElements) {
                                ResultCorrespondence correspondence = (ResultCorrespondence) element;
                                resultsCorrespondence.add(correspondence);
                            }
                        }

                        if (resultsCorrespondence != null) {
                            for(CorrespondenceVariant var : allVariants) {
                                //for (ResultCorrespondence correspondenceResult : resultsCorrespondence) {
                                    List<AnswerVariant> correspondenceAnswers = var.getCorrectAnswers();

                                    row = sheet.createRow(rownum++);
                                    Cell correctTitleCell = row.createCell(0);
                                    correctTitleCell.setCellStyle(markStyle);
                                    correctTitleCell.setCellValue("Вариант:");
                                    CellUtil.setCellStyleProperty(correctTitleCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                                    CellUtil.setFont(correctTitleCell, styles.getBoldFont());
                                    cell = row.createCell(2);
                                    cell.setCellStyle( styles.getBodyStyle());
                                    cell.setCellValue(var.getText());

                                    if(correspondenceAnswers != null) {
                                        for (AnswerVariant answer : correspondenceAnswers) {
                                            row = sheet.createRow(rownum++);
                                            Cell correctCorrespondenceCell = row.createCell(0);
                                            correctCorrespondenceCell.setCellStyle(markStyle);
                                            correctCorrespondenceCell.setCellValue("Верный ответ:");
                                            CellUtil.setCellStyleProperty(correctCorrespondenceCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                                            CellUtil.setFont(correctCorrespondenceCell, styles.getBoldFont());
                                            cell = row.createCell(2);
                                            cell.setCellStyle( styles.getBodyStyle());
                                            cell.setCellValue(answer.getText());
                                        }
                                    }
                                    if(resultsCorrespondence != null) {
                                        Boolean answered = false;
                                        for ( ResultCorrespondence resultCorrespondence : resultsCorrespondence ) {
                                            if( resultCorrespondence.getCorrespondenceVariant() == var ) {
                                                row = sheet.createRow(rownum++);
                                                Cell chosenTitleCell = row.createCell(0);
                                                chosenTitleCell.setCellStyle(markStyle);
                                                chosenTitleCell.setCellValue("Выбранный ответ:");
                                                CellUtil.setCellStyleProperty(chosenTitleCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                                                CellUtil.setFont(chosenTitleCell, styles.getBoldFont());
                                                cell = row.createCell(1);
                                                cell.setCellStyle( styles.getBodyStyle());
                                                cell.setCellValue(resultCorrespondence.isCorrect() ? "верно" : "неверно");
                                                cell = row.createCell(2);
                                                cell.setCellStyle( styles.getBodyStyle());
                                                cell.setCellValue(resultCorrespondence.getAnswerVariant().getText());
                                                answered = true;
                                            }
                                        }
                                        if (!answered) {
                                            row = sheet.createRow(rownum++);
                                            Cell chosenTitleCell = row.createCell(0);
                                            chosenTitleCell.setCellStyle(markStyle);
                                            chosenTitleCell.setCellValue("Выбранный ответ:");
                                            CellUtil.setCellStyleProperty(chosenTitleCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                                            CellUtil.setFont(chosenTitleCell, styles.getBoldFont());
                                            cell = row.createCell(1);
                                            cell.setCellStyle( styles.getBodyStyle());
                                            cell.setCellValue("неверно");
                                            cell = row.createCell(2);
                                            cell.setCellStyle( styles.getBodyStyle());
                                            cell.setCellValue("Не выбран");
                                        }
                                    }
                                //}
                            }
                        }
                    }

                    /*
                     * for simple_order questions
                     */
                    if(questionResult.getQuestion().getType().equals(QuestionType.SIMPLE_ORDER)) {
                        QuestionSimpleOrder question = (QuestionSimpleOrder) questionResult.getQuestion();
                        List<ResultSimpleOrder> orderResults = new ArrayList<>();
                        List<ResultElement> elements = questionResult.getElements();

                        for (ResultElement element : elements) {
                            ResultSimpleOrder resultSimpleOrder = (ResultSimpleOrder) element;
                            orderResults.add(resultSimpleOrder);
                        }

                        orderResults.sort(new Comparator<ResultSimpleOrder>() {
                            @Override
                            public int compare( ResultSimpleOrder o1, ResultSimpleOrder o2 ) {
                                return Integer.compare( o1.getEnteredOrder(), o2.getEnteredOrder() );
                            }
                        });

                        int order = 1;
                        for ( ResultSimpleOrder resultSimpleOrder : orderResults ) {
                            if ( resultSimpleOrder.getEnteredOrder() == order ) {
                                row = sheet.createRow(rownum++);
                                Cell chosenTitleCell = row.createCell(0);
                                chosenTitleCell.setCellStyle(markStyle);
                                chosenTitleCell.setCellValue("Выбранный ответ ( № " + resultSimpleOrder.getEnteredOrder() + " ):");
                                CellUtil.setCellStyleProperty(chosenTitleCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                                CellUtil.setFont(chosenTitleCell, styles.getBoldFont());
                                cell = row.createCell(1);
                                cell.setCellStyle( styles.getBodyStyle());
                                cell.setCellValue(resultSimpleOrder.isCorrect() ? "верно" : "неверно, правильный №: " + resultSimpleOrder.getAnswerVariant().getSequenceOrder());
                                cell = row.createCell(2);
                                cell.setCellStyle( styles.getBodyStyle());
                                cell.setCellValue(resultSimpleOrder.getAnswerVariant().getText());
                                order++;
                            } else {
                                row = sheet.createRow(rownum++);
                                Cell chosenTitleCell = row.createCell(0);
                                chosenTitleCell.setCellStyle(markStyle);
                                chosenTitleCell.setCellValue("Выбранный ответ ( № " + order + " ):");
                                CellUtil.setCellStyleProperty(chosenTitleCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                                CellUtil.setFont(chosenTitleCell, styles.getBoldFont());
                                cell = row.createCell(1);
                                cell.setCellStyle( styles.getBodyStyle());
                                cell.setCellValue("неверно, правильный вариант №: " + resultSimpleOrder.getAnswerVariant().getSequenceOrder());
                                cell = row.createCell(2);
                                cell.setCellStyle( styles.getBodyStyle());
                                cell.setCellValue("Не выбран");
                                order++;
                            }
                        }

                    }

                    /*
                     * for tree questions
                     */
                    if(questionResult.getQuestion().getType().equals(QuestionType.TREE)) {
                        QuestionTree question = (QuestionTree) questionResult.getQuestion();
                        List<CorrespondenceVariant> allVariants = question.getCorrespondenceVariants();
                        List<ResultElement> resultElements = questionResult.getElements();
                        List<ResultTree> resultsTree = new ArrayList<>();

                        if(resultElements != null) {
                            for (ResultElement element : resultElements) {
                                ResultTree tree = (ResultTree) element;
                                resultsTree.add(tree);
                            }
                        }

                        if (!resultsTree.isEmpty()) {
                            for(CorrespondenceVariant var : allVariants) {
                                List<AnswerVariant> correspondenceAnswers = var.getCorrectAnswers();
                                List<AnswerVariant> correctAnswers = new ArrayList<>();

                                for ( AnswerVariant answerVariant : correspondenceAnswers ) {
                                    if ( answerVariant.isCorrect() ) {
                                        correctAnswers.add(answerVariant);
                                    }
                                }

                                row = sheet.createRow(rownum++);
                                Cell correctTitleCell = row.createCell(0);
                                correctTitleCell.setCellStyle(markStyle);
                                correctTitleCell.setCellValue("Вариант:");
                                CellUtil.setCellStyleProperty(correctTitleCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                                CellUtil.setFont(correctTitleCell, styles.getBoldFont());
                                cell = row.createCell(2);
                                cell.setCellStyle( styles.getBodyStyle());
                                cell.setCellValue(var.getText());

                                if(correspondenceAnswers != null) {
                                    for (AnswerVariant answer : correctAnswers) {
                                        row = sheet.createRow(rownum++);
                                        Cell correctCorrespondenceCell = row.createCell(0);
                                        correctCorrespondenceCell.setCellStyle(markStyle);
                                        correctCorrespondenceCell.setCellValue("Верный ответ:");
                                        CellUtil.setCellStyleProperty(correctCorrespondenceCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                                        CellUtil.setFont(correctCorrespondenceCell, styles.getBoldFont());
                                        cell = row.createCell(2);
                                        cell.setCellStyle( styles.getBodyStyle());
                                        cell.setCellValue(answer.getText());
                                    }
                                }
                                if(resultsTree != null) {
                                    Boolean answered = false;
                                    for ( ResultTree resultTree : resultsTree ) {
                                        if( resultTree.getCorrespondenceVariant() == var ) {
                                            row = sheet.createRow(rownum++);
                                            Cell chosenTitleCell = row.createCell(0);
                                            chosenTitleCell.setCellStyle(markStyle);
                                            chosenTitleCell.setCellValue("Выбранный ответ:");
                                            CellUtil.setCellStyleProperty(chosenTitleCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                                            CellUtil.setFont(chosenTitleCell, styles.getBoldFont());
                                            cell = row.createCell(1);
                                            cell.setCellStyle( styles.getBodyStyle());
                                            cell.setCellValue(resultTree.isCorrect() ? "верно" : "неверно");
                                            cell = row.createCell(2);
                                            cell.setCellStyle( styles.getBodyStyle());
                                            cell.setCellValue(resultTree.getAnswerVariant().getText());
                                            answered = true;
                                        }
                                    }
                                    if (!answered) {
                                        row = sheet.createRow(rownum++);
                                        Cell chosenTitleCell = row.createCell(0);
                                        chosenTitleCell.setCellStyle(markStyle);
                                        chosenTitleCell.setCellValue("Выбранный ответ:");
                                        CellUtil.setCellStyleProperty(chosenTitleCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                                        CellUtil.setFont(chosenTitleCell, styles.getBoldFont());
                                        cell = row.createCell(1);
                                        cell.setCellStyle( styles.getBodyStyle());
                                        cell.setCellValue("неверно");
                                        cell = row.createCell(2);
                                        cell.setCellStyle( styles.getBodyStyle());
                                        cell.setCellValue("Не выбран");
                                    }
                                }
                            }
                        }
                    }


                    row = sheet.createRow(rownum++);
                    Cell scoreTitleCell = row.createCell(0);
                    scoreTitleCell.setCellStyle( markStyle );
                    scoreTitleCell.setCellValue( "Исходный балл:" );
                    CellUtil.setCellStyleProperty(scoreTitleCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                    CellUtil.setFont(scoreTitleCell, styles.getBoldFont());
                    cell = row.createCell(1);
                    cell.setCellStyle( styles.getBodyStyle() );
                    cell.setCellValue(
                            String.format( "%d (из %d) * %d",
                                    questionResult.getScore(),
                                    questionResult.getQuestion().getQuestionInfo().getMaxScore(),
                                    questionResult.getScoreCost() )
                    );

                    Cell ballTitleCell = row.createCell(2);
                    ballTitleCell.setCellStyle( markStyle );
                    ballTitleCell.setCellValue( "Оценка:" );
                    CellUtil.setCellStyleProperty(ballTitleCell, CellUtil.ALIGNMENT, HorizontalAlignment.RIGHT);
                    CellUtil.setFont(ballTitleCell, styles.getBoldFont());
                    cell = row.createCell(3);
                    cell.setCellStyle( styles.getBodyStyle() );
                    cell.setCellValue( questionResult.getMark() );

                    markStyle.setAlignment(HorizontalAlignment.CENTER);
                }
            }

            Row finalMarkRow = sheet.createRow(rownum++);
            Cell finalMarkCell = CellUtil.createCell(finalMarkRow, 0, "Результирующая оценка");
            CellUtil.setAlignment(finalMarkCell, HorizontalAlignment.LEFT);
            CellUtil.setFont(finalMarkCell, styles.getBoldFont());

            CellUtil.setCellStyleProperties(finalMarkCell, borders);
            CellUtil.setCellStyleProperty(finalMarkCell, CellUtil.BORDER_RIGHT, BorderStyle.NONE);

            Cell finalMarkEmptyCell = CellUtil.createCell(finalMarkRow, 1, "");
            CellUtil.setAlignment(finalMarkCell, HorizontalAlignment.LEFT);
            CellUtil.setFont(finalMarkCell, styles.getBoldFont());

            Cell totalMarkCell = finalMarkRow.createCell(1);
            totalMarkCell.setCellStyle( markStyle );
            totalMarkCell.setCellValue( result.getMarkTotal() );
            CellUtil.setCellStyleProperty(totalMarkCell, CellUtil.BORDER_LEFT, BorderStyle.NONE);

            //empty row nr. 2
            Row rowEmpty1 = sheet.createRow(rownum++);
            Cell cellEmpty1 = rowEmpty1.createCell(0);
            cellEmpty1.setCellType(CellType.BLANK);

            Row appealRow = sheet.createRow(rownum++);
            sheet.addMergedRegion(new CellRangeAddress(rownum - 1,rownum - 1,0,2));
            Cell appealCell = CellUtil.createCell(appealRow, 0, "Результат аппеляции: _________________________________________");
            CellUtil.setFont(appealCell, styles.getDefaultFont());
            CellUtil.setCellStyleProperty(appealCell, CellUtil.WRAP_TEXT, true);
            CellUtil.setAlignment(appealCell, HorizontalAlignment.LEFT);

            Row acknowledgeRow = sheet.createRow(rownum++);
            sheet.addMergedRegion(new CellRangeAddress(rownum - 1,rownum - 1,0,2));
            Cell acknowledgeCell = CellUtil.createCell(acknowledgeRow, 0, "С результатами вступительного испытания ознакомлен.");
            CellUtil.setFont(acknowledgeCell, styles.getDefaultFont());
            CellUtil.setCellStyleProperty(acknowledgeCell, CellUtil.WRAP_TEXT, true);
            CellUtil.setAlignment(acknowledgeCell, HorizontalAlignment.LEFT);

            Row acknowledgeRow1 = sheet.createRow(rownum++);
            sheet.addMergedRegion(new CellRangeAddress(rownum - 1,rownum - 1,0,2));
            Cell acknowledgeCell1 = CellUtil.createCell(acknowledgeRow1, 0, "С результатами аппеляции согласен.");
            CellUtil.setFont(acknowledgeCell1, styles.getDefaultFont());
            CellUtil.setAlignment(acknowledgeCell1, HorizontalAlignment.LEFT);

            Row acknowledgeRow2 = sheet.createRow(rownum++);
            sheet.addMergedRegion(new CellRangeAddress(rownum - 1,rownum - 1,0,2));
            Cell acknowledgeCell2 = CellUtil.createCell(acknowledgeRow2, 0, "Претензий к приёмной комиссии не имею.");
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


        return null;
    }
}
