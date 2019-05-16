package ru.rsmu.tempoLW.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.utils.builder.QuestionBuilder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
            wb = new HSSFWorkbook();
        }
        else {
            wb = new XSSFWorkbook();
        }
        warning = new LinkedList<>();

        Sheet sheet = wb.createSheet( subject.getTitle() );

        List<Question> questions = questionDao.findSubjectQuestions( subject );
        Map<Object, QuestionBuilder> builders = new HashMap<>();

        createHeader( sheet );
        int rowN = 1; /* header */
        for ( Question question : questions ) {
            QuestionBuilder builder = builders.get( question.getClass() );
            if ( builder == null ) {
                builder = QuestionBuilder.create( question );
                builders.put( question.getClass(), builder );
            }
            rowN = builder.write( sheet, rowN, question );
        }
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

        cell = row.createCell( COLUMN_ROW_TYPE );
        cell.setCellValue( "Тип строки" );
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
    }
}
