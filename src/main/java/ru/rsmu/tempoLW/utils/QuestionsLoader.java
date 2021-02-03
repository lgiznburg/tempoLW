package ru.rsmu.tempoLW.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.entities.QuestionInfo;
import ru.rsmu.tempoLW.entities.SubTopic;
import ru.rsmu.tempoLW.utils.builder.QuestionBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author leonid.
 */
public class QuestionsLoader extends ExcelReader implements ExcelLayout {
    private QuestionDao questionDao;
    private ExamSubject subject;

    private Workbook wb;
    private List<String> warning;

    private ImagesExtractor imagesExtractor;

    public QuestionsLoader( QuestionDao questionDao, ExamSubject subject ) {
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

    public List<String> loadFromFile() {

        Sheet sheet = wb.getSheetAt( 0 );  // main page

        List<Question> questions = new LinkedList<>();

        QuestionBuilder builder = null;
        for ( Iterator<Row> rowIterator = sheet.rowIterator(); rowIterator.hasNext(); ) {
            Row row = rowIterator.next();
            if ( row.getRowNum() == 0 ) { // skip header
                continue;
            }
            String questionType = getCellValue( row, COLUMN_QUESTION_TYPE );

            if ( StringUtils.isNotBlank( questionType ) ) {
                // parse question row. only question could have not empty question type info
                QuestionInfo questionInfo = createQuestionInfo( row );
                if ( questionInfo != null ) {
                    builder = QuestionBuilder.create( questionType );
                    builder.setImagesExtractor( imagesExtractor );
                    builder.setQuestionDao( questionDao );
                    builder.parse( row );
                    Question question = builder.getResult();
                    question.setQuestionInfo( questionInfo );
                    //question.setCreatedDate( new Date() );
                    questions.add( question );
                }
                else {
                    builder = null;  // this question parsing has not started, it needs to ignore rows till another question
                }
            }
            else if ( builder != null ) {
                builder.parse( row );
            }
        }
        // save all created/updated question
        questions.forEach( q -> {
            questionDao.save( q.getQuestionInfo() );
            questionDao.save( q );
        } );

        return warning;
    }

    protected QuestionInfo createQuestionInfo( Row row ) {
        // 1. Create or find the topic
        String topicTitle = getCellValue( row, COLUMN_TOPIC );
        SubTopic topic = questionDao.findTopicByName( topicTitle, subject );
        if ( topic == null ) {
            topic = new SubTopic();
            topic.setTitle( topicTitle );
            topic.setSubject( subject );
            questionDao.save( topic );
        }

        // 2. get question info values
        String name = getCellValue( row, COLUMN_NAME );
        Long complexity = getCellNumber( row, COLUMN_COMPLEXITY );
        Long maxScore = getCellNumber( row, COLUMN_MAX_SCORE );
        if ( name == null || name.isEmpty() ) {
            //warning.add( String.format( "Error: Line %d. Question has no name.", row.getRowNum() ) );
            name = topicTitle;
        }
        if ( complexity == null || maxScore == null ) {
            warning.add( String.format( "Error: Line %d. Question has no score nor complexity.", row.getRowNum() ) );
            return null;
        }

        // 3. find or create QuestionInfo
        Long questionId = getCellNumber( row, COLUMN_ID );
        QuestionInfo questionInfo = null;
        if ( questionId != null && questionId > 1 ) {
            Question question = questionDao.find( Question.class, questionId );
            if ( question != null ) {
                questionInfo = question.getQuestionInfo();
                if ( subject.getId() != questionInfo.getSubject().getId() ) {
                    questionInfo = null;  // question ties to another subject, need to create new question
                }
            }
        }
        if ( questionInfo == null ) {
            questionInfo = new QuestionInfo();
        }
        // 4. Update / populate
        questionInfo.setComplexity( complexity.intValue() );
        questionInfo.setMaxScore( maxScore.intValue() );
        questionInfo.setName( name );
        questionInfo.setTopic( topic );
        questionInfo.setSubject( subject );
        /*Long questionCode = getCellNumber( row, COLUMN_CODE );
        if ( questionCode != null && questionCode > 0 ) {
            questionInfo.setCode( questionCode.intValue() );
        }*/
        //questionDao.save( questionInfo );

        return questionInfo;
    }

    public ImagesExtractor getImagesExtractor() {
        return imagesExtractor;
    }

    public void setImagesExtractor( ImagesExtractor imagesExtractor ) {
        this.imagesExtractor = imagesExtractor;
    }

    public List<String> getWarning() {
        return warning;
    }
}
