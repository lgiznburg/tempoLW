package ru.rsmu.tempoLW.utils.builder;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.rsmu.tempoLW.entities.AnswerVariant;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.entities.QuestionSimple;
import ru.rsmu.tempoLW.entities.UploadedImage;

import java.util.LinkedList;

/**
 * @author leonid.
 */
public class SimpleQuestionBuilder extends QuestionBuilder {
    protected SimpleQuestionBuilder() {
    }

    @Override
    public int parse( Row row ) {
        String type = getCellValue( row, COLUMN_QUESTION_TYPE );

        if ( type != null ) {
            // this is question row. should we check question type again?
            QuestionSimple question = null;
            Long questionId = getCellNumber( row, COLUMN_ID );
            if ( questionId != null && questionId > 0 ) {
                question = getQuestionDao().find( QuestionSimple.class, questionId );
            }
            if ( question == null ) {
                question = new QuestionSimple();
                question.setAnswerVariants( new LinkedList<>() );
            }

            question.setText( getCellValue( row, COLUMN_TEXT ) );

            UploadedImage uploadedImage = checkUploadedImage( row );
            if ( uploadedImage != null ) {
                question.setImage( uploadedImage );
            }
            this.result = question;
        }
        else if ( result != null ){
            // answer row
            String text = getCellValue( row, COLUMN_TEXT );
            if ( StringUtils.isNotBlank( text ) ) {
                Long answerId = getCellNumber( row, COLUMN_ID );
                AnswerVariant answerVariant = null;
                if ( answerId != null && answerId > 0 ) {
                    answerVariant = getQuestionDao().find( AnswerVariant.class, answerId );
                }
                if ( answerVariant == null ) {
                    answerVariant = new AnswerVariant();
                }
                answerVariant.setText( text );
                answerVariant.setCorrect( getCellNumber( row, COLUMN_RIGHTNESS ) != null );
                answerVariant.setQuestion( result );
                ((QuestionSimple)result).getAnswerVariants().add( answerVariant );
                UploadedImage uploadedImage = checkUploadedImage( row );
                if ( uploadedImage != null ) {
                    answerVariant.setImage( uploadedImage );
                }
            }
        }

        return 0;
    }

    @Override
    public int write( Sheet sheet, int rowN, Question question ) {
        Row row = sheet.createRow( rowN++ );
        writeQuestionInfo( row, question, SIMPLE_TYPE );

        return writeAnswers( sheet, rowN, ((QuestionSimple)question).getAnswerVariants() );
    }
}
