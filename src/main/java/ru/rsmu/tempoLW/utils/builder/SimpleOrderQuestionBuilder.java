package ru.rsmu.tempoLW.utils.builder;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.rsmu.tempoLW.entities.AnswerVariant;
import ru.rsmu.tempoLW.entities.QuestionSimple;
import ru.rsmu.tempoLW.entities.UploadedImage;

import java.util.LinkedList;

/**
 * @author leonid.
 */
public class SimpleOrderQuestionBuilder extends QuestionBuilder{
    protected SimpleOrderQuestionBuilder() {
    }

    @Override
    public int parse( Sheet sheet, int rowN ) {
        QuestionSimple question = new QuestionSimple();
        question.setAnswerVariants( new LinkedList<>() );

        Row row = sheet.getRow( rowN );
        question.setText( getCellValue( row, COLUMN_TEXT ) );

        UploadedImage uploadedImage = checkUploadedImage( row );
        if ( uploadedImage != null ) {
            question.setImage( uploadedImage );
        }

        int answerOrder = 1;
        do {
            row = sheet.getRow( ++rowN );
            if ( row == null || row.getCell( COLUMN_ROW_TYPE ) == null ) {
                break; // empty row
            }
            String rowType = getCellValue( row, COLUMN_ROW_TYPE );
            if ( ANSWER_ROW.equalsIgnoreCase( rowType ) ) {
                AnswerVariant answerVariant = new AnswerVariant();
                answerVariant.setText( getCellValue( row, COLUMN_TEXT ) );
                answerVariant.setCorrect( getCellNumber( row, COLUMN_RIGHTNESS ) != null );
                answerVariant.setQuestion( question );
                if ( answerVariant.isCorrect() ) {
                    answerVariant.setSequenceOrder( answerOrder++ );
                }
                question.getAnswerVariants().add( answerVariant );
                uploadedImage = checkUploadedImage( row );
                if ( uploadedImage != null ) {
                    answerVariant.setImage( uploadedImage );
                }
            }
            else {
                break;
            }

        } while ( true );

        this.result = question;
        return rowN;
    }
}
