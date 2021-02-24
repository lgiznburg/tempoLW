package ru.rsmu.tempoLW.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.utils.builder.QuestionBuilder;

import java.util.*;

/**
 * @author leonid.
 *
 * Class for export questions of given subject
 * Create excel workbook
 */
public class QuestionExporter implements ExcelLayout {
    private QuestionDao questionDao;
    private ExamSubject subject;

    private Workbook wb;
    private List<String> warning;

    public QuestionExporter( QuestionDao questionDao, ExamSubject subject ) {
        this.questionDao = questionDao;
        this.subject = subject;
    }

    public void doExport( boolean excel2007 ) {
        if ( excel2007 ) {
            wb = new XSSFWorkbook(); // excel 2007 format
        } else {
            wb = new HSSFWorkbook(); // old excel
        }
        warning = new LinkedList<>();

        Sheet sheet = wb.createSheet( subject.getTitle() );
        /*CellStyle lockedStyle = wb.createCellStyle();
        lockedStyle.setLocked( true );
        CellStyle unlockedStyle = wb.createCellStyle();
        unlockedStyle.setLocked( false );*/
        CellStyle textStyle = wb.createCellStyle();
        textStyle.setWrapText( true );
        textStyle.setLocked( false );

        if ( excel2007 ) {
/*
            XSSFSheet xssfSheet = (XSSFSheet)sheet;
            CTCol col = xssfSheet.getCTWorksheet().getColsArray(0).addNewCol();
            col.setMin( COLUMN_TOPIC + 1);  // unlock from TOPIC
            col.setMax( COLUMN_TEXT );  // till IMAGE
            //col.setWidth(9.15);
            col.setStyle(unlockedStyle.getIndex());

            col = xssfSheet.getCTWorksheet().getColsArray(0).addNewCol();
            col.setMin( COLUMN_TEXT );  // unlock from text
            col.setMax( COLUMN_TEXT + 1 );  //
            col.setStyle(textStyle.getIndex());

            col = xssfSheet.getCTWorksheet().getColsArray(0).addNewCol();
            col.setMin( COLUMN_TEXT + 1);  // unlock from TOPIC
            col.setMax( COLUMN_IMAGE + 2 );  // till IMAGE
            //col.setWidth(9.15);
            col.setStyle(unlockedStyle.getIndex());
            xssfSheet.lockInsertRows( false );
            xssfSheet.lockDeleteRows( false );
            xssfSheet.lockFormatRows( false );
*/
        }
        //sheet.protectSheet( "id_protected" );

        List<Question> questions = questionDao.findSubjectQuestions( subject );
        Map<Object, QuestionBuilder> builders = new HashMap<>();

        createHeader( sheet );
        int rowN = 1; /* header */
        for ( Question question : questions ) {
            QuestionBuilder builder = builders.get( question.getClass() );
            if ( builder == null ) {
                builder = QuestionBuilder.create( question.getType() );
                builders.put( question.getClass(), builder );
            }
            rowN = builder.write( sheet, rowN, question );
        }

        for ( Iterator<Row> rowIterator = sheet.rowIterator(); rowIterator.hasNext(); ) {
            Row row = rowIterator.next();
            for ( Iterator<Cell> cellIterator = row.cellIterator(); cellIterator.hasNext(); ) {
                Cell cell = cellIterator.next();
                /*if ( cell.getColumnIndex() == COLUMN_ID || cell.getColumnIndex() == COLUMN_QUESTION_TYPE ) {
                    cell.setCellStyle( lockedStyle );
                }
                else */if ( cell.getColumnIndex() == COLUMN_TOPIC || cell.getColumnIndex() == COLUMN_TEXT ) {
                    cell.setCellStyle( textStyle );
                }
                /*else {
                    cell.setCellStyle( unlockedStyle );
                }*/
            }
        }
        //sheet.setDefaultRowHeight( (short) -1 );
        sheet.setDefaultColumnWidth( 10 ); // in characters
        sheet.setColumnWidth( COLUMN_TOPIC, 16*256 ); // in units (char/256)
        sheet.setColumnWidth( COLUMN_TEXT, 30*256 );

    }

    public Workbook getWb() {
        return wb;
    }

    public void setWb( Workbook wb ) {
        this.wb = wb;
    }

    public List<String> getWarning() {
        return warning;
    }

    public void setWarning( List<String> warning ) {
        this.warning = warning;
    }

    private void createHeader( Sheet sheet ) {
        Row row = sheet.createRow( 0 );
        Cell cell;

        cell = row.createCell( COLUMN_QUESTION_TYPE );
        cell.setCellValue( "Тип вопроса" );
        cell = row.createCell( COLUMN_TOPIC );
        cell.setCellValue( "Раздел (тема)" );
        cell = row.createCell( COLUMN_COMPLEXITY );
        cell.setCellValue( "Сложность" );
        cell = row.createCell( COLUMN_MAX_SCORE );
        cell.setCellValue( "Макс. балл" );
        cell = row.createCell( COLUMN_NAME );
        cell.setCellValue( "Название" );
        //cell = row.createCell( COLUMN_VARIANT );
        //cell.setCellValue( "" );
        cell = row.createCell( COLUMN_CODE );
        cell.setCellValue( "Код" );
        cell = row.createCell( COLUMN_TEXT );
        cell.setCellValue( "Текст" );
        cell = row.createCell( COLUMN_RIGHTNESS );
        cell.setCellValue( "Правильность" );
        cell = row.createCell( COLUMN_IMAGE );
        cell.setCellValue( "Изображение" );
        cell = row.createCell( COLUMN_ID );
        cell.setCellValue( "Служебный код, не изменять!" );
    }
}
