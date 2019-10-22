package ru.rsmu.tempoLW.utils.builder;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.utils.ExcelLayout;
import ru.rsmu.tempoLW.utils.ExcelReader;
import ru.rsmu.tempoLW.utils.ImagesExtractor;

import java.util.List;
import java.util.Map;

/**
 * @author leonid.
 */
public abstract class QuestionBuilder extends ExcelReader implements ExcelLayout {

    protected Question result;

    protected ImagesExtractor imagesExtractor;

    public static QuestionBuilder create( String questionType ) throws IllegalArgumentException {
        if ( questionType.equalsIgnoreCase( SIMPLE_TYPE ) ) {
            return new SimpleQuestionBuilder();
        }
        else if ( questionType.equalsIgnoreCase( OPEN_TYPE )) {
            return new OpenQuestionBuilder();
        }
        else if ( questionType.equalsIgnoreCase( CORRESPONDENCE_TYPE )) {
            return  new CorrespondenceQuestionBuilder();
        }
        else if ( questionType.equalsIgnoreCase( SIMPLE_ORDER_TYPE )) {
            return  new SimpleOrderQuestionBuilder();
        }
        else if ( questionType.equalsIgnoreCase( TREE_TYPE )) {
            return  new TreeQuestionBuilder();
        }
        throw new IllegalArgumentException( "Incorrect type of question" );
    }

    public static QuestionBuilder create( Question question ) throws IllegalArgumentException {
        if ( question instanceof QuestionSimple ) {
            return new SimpleQuestionBuilder();
        }
        else if ( question instanceof QuestionOpen ) {
            return new OpenQuestionBuilder();
        }
        else if ( question instanceof QuestionCorrespondence ) {
            return  new CorrespondenceQuestionBuilder();
        }
        else if ( question instanceof QuestionSimpleOrder ) {
            return  new SimpleOrderQuestionBuilder();
        }
        else if ( question instanceof QuestionTree ) {
            return  new TreeQuestionBuilder();
        }
        throw new IllegalArgumentException( "Incorrect type of question" );
    }

    /**
     * Read a question from the file
     * @param sheet Sheet in excel file
     * @param rowN number of row with current question
     * @return number of row after reading (next question)
     */
    public abstract int parse( Sheet sheet, int rowN );

    /**
     * Write the question into excel file
     * @param sheet Sheet in excel file
     * @param rowN number of row for current question
     * @param question question to write
     * @return number of row for next question
     */
    public abstract int write( Sheet sheet, int rowN, Question question );

    public Question getResult() {
        return result;
    }

    public ImagesExtractor getImagesExtractor() {
        return imagesExtractor;
    }

    public void setImagesExtractor( ImagesExtractor imagesExtractor ) {
        this.imagesExtractor = imagesExtractor;
    }

    UploadedImage checkUploadedImage( Row row ) {
        if ( imagesExtractor != null ) {
            String imageName = getCellValue( row, COLUMN_IMAGE );
            if ( imageName != null && !imageName.isEmpty() ) {
                byte[] picture = imagesExtractor.getPicture( imageName );
                if ( picture != null ) {
                    UploadedImage uploadedImage = new UploadedImage();
                    uploadedImage.setSourceName( imageName );
                    uploadedImage.setPicture( picture );
                    uploadedImage.setContentType( imagesExtractor.getContentType( imageName ) );
                    return uploadedImage;
                }
            }
        }
        return null;
    }

    void writeQuestionInfo( Row row, Question question, String questionType ) {
        Cell cell;
        cell = row.createCell( COLUMN_ROW_TYPE );
        cell.setCellValue( QUESTION_ROW );

        cell = row.createCell( COLUMN_QUESTION_TYPE );
        cell.setCellValue( questionType );

        cell = row.createCell( COLUMN_TOPIC );
        cell.setCellValue( question.getQuestionInfo().getTopic().getTitle() );

        cell = row.createCell( COLUMN_COMPLEXITY );
        cell.setCellValue( question.getQuestionInfo().getComplexity() );

        cell = row.createCell( COLUMN_MAX_SCORE );
        cell.setCellValue( question.getQuestionInfo().getMaxScore() );

        if ( !question.getQuestionInfo().getName().equals( question.getQuestionInfo().getTopic().getTitle() ) ) {
            cell = row.createCell( COLUMN_NAME );
            cell.setCellValue( question.getQuestionInfo().getName() );
        }

        if ( question.getQuestionInfo().getCode() > 0 ) {
            cell = row.createCell( COLUMN_CODE );
            cell.setCellValue( question.getQuestionInfo().getCode() );
        }

        cell = row.createCell( COLUMN_TEXT );
        cell.setCellValue( question.getText() );

        if ( question.getImage() != null ) {
            cell = row.createCell( COLUMN_IMAGE );
            cell.setCellValue( question.getImage().getSourceName() );
        }
    }

    int writeAnswers( Sheet sheet, int rowN, List<AnswerVariant> answers ) {
        return writeAnswers( sheet, rowN, answers, null );
    }

    int writeAnswers( Sheet sheet, int rowN, List<AnswerVariant> answers, Map<Long, String> codes ) {
        Row row;
        Cell cell;

        for ( AnswerVariant answerVariant : answers ) {
            row = sheet.createRow( rowN++ );

            cell = row.createCell( COLUMN_ROW_TYPE );
            cell.setCellValue( ANSWER_ROW );

            cell = row.createCell( COLUMN_TEXT );
            cell.setCellValue( answerVariant.getText() );

            if ( codes != null ) {
                String rightness = codes.get( answerVariant.getId() );
                if ( rightness != null ) {
                    cell = row.createCell( COLUMN_RIGHTNESS );
                    cell.setCellValue( rightness );
                }
            }
            else {
                if ( answerVariant.isCorrect() ) {
                    cell = row.createCell( COLUMN_RIGHTNESS );
                    cell.setCellValue( 1 );
                }
            }
        }
        return rowN;

    }
}
