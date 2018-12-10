package ru.rsmu.tempoLW.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.entities.QuestionInfo;
import ru.rsmu.tempoLW.entities.SubTopic;
import ru.rsmu.tempoLW.entities.TestSubject;
import ru.rsmu.tempoLW.utils.builder.QuestionBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * @author leonid.
 */
public class QuestionsLoader extends ExcelLayout {
    private QuestionDao questionDao;
    private TestSubject subject;

    private Workbook wb;
    private List<String> warning;

    public QuestionsLoader( QuestionDao questionDao, TestSubject subject ) {
        this.questionDao = questionDao;
        this.subject = subject;
    }

    public QuestionsLoader createWorkbook( InputStream input, boolean excel2007 ) throws IOException {
        if ( excel2007 ) {
            wb = new HSSFWorkbook( input );
        }
        else {
            wb = new XSSFWorkbook( input );
        }
        warning = new LinkedList<>();
        return this;
    }

    public List<String> loadFromFile( InputStream input ) throws IOException {

        Sheet sheet = wb.getSheetAt( 0 );  // main page

        int rowN = 1;  // skip header
        boolean ignoreEmptyRow = true;
        do {
            Row row = sheet.getRow( rowN );
            // check if row is valid
            if ( row == null || row.getCell( (short) 0 ) == null ) {
                if ( ignoreEmptyRow ) {
                    rowN++;
                    ignoreEmptyRow = false;
                    continue;
                }
                else {
                    break;
                }
            }
            String rowType = getCellValue( row, COLUMN_ROW_TYPE );
            if ( rowType.equalsIgnoreCase( QUESTION_ROW ) ) {
                ignoreEmptyRow = true;
                QuestionInfo questionInfo = createQuestionInfo( row );
                if ( questionInfo != null ) {
                    String questionType = getCellValue( row, COLUMN_QUESTION_TYPE );
                    QuestionBuilder builder = QuestionBuilder.create( questionType );
                    rowN = builder.parse( sheet, rowN );
                    Question question = builder.getResult();
                    question.setQuestionInfo( questionInfo );
                    questionDao.save( question );
                    continue;
                }

            }
            rowN++;

        } while ( true );
        return null;
    }

    protected QuestionInfo createQuestionInfo( Row row ) {
        String topicTitle = getCellValue( row, COLUMN_TOPIC );
        SubTopic topic = questionDao.findTopicByName( topicTitle );
        if ( topic == null ) {
            topic = new SubTopic();
            topic.setTitle( topicTitle );
            topic.setSubject( subject );
            questionDao.save( topic );
        }

        String name = getCellValue( row, COLUMN_NAME );
        Long complexity = getCellNumber( row, COLUMN_COMPLEXITY );
        Long maxScore = getCellNumber( row, COLUMN_MAX_SCORE );
        if ( name == null || name.isEmpty() || complexity == null || maxScore == null ) {
            warning.add( String.format( "Error: Line %d. Question has no name or score or complexity.", row.getRowNum() ) );
            return null;
        }

        Long variant = getCellNumber( row, COLUMN_VARIANT );
        QuestionInfo questionInfo = null;
        if ( variant != null && variant > 1 ) {
            questionInfo = questionDao.findQuestionInfoByName( name, topic, complexity.intValue() );
        }

        if ( questionInfo == null ) {
            questionInfo = new QuestionInfo();
            questionInfo.setComplexity( complexity.intValue() );
            questionInfo.setMaxScore( maxScore.intValue() );
            questionInfo.setName( name );
            questionInfo.setTopic( topic );
            questionInfo.setSubject( subject );
            questionDao.save( questionInfo );
        } else {
            if ( questionInfo.getMaxScore() != maxScore ) {
                warning.add( String.format( "Warning: Line %d, Question \"%s\". Another variant with different max score exists.", row.getRowNum(), name ) );
            }
        }

        return questionInfo;
    }


}
