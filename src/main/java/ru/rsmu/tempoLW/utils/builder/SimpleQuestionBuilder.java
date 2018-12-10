package ru.rsmu.tempoLW.utils.builder;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;

import java.util.LinkedList;

/**
 * @author leonid.
 */
public class SimpleQuestionBuilder extends QuestionBuilder {
    protected SimpleQuestionBuilder() {
    }

    @Override
    public int parse( Sheet sheet, int rowN ) {
        QuestionSimple question = new QuestionSimple();
        question.setAnswerVariants( new LinkedList<>() );

        Row row = sheet.getRow( rowN );
        question.setText( getCellValue( row, COLUMN_TEXT ) );

        do {
            row = sheet.getRow( ++rowN );
            if ( row == null || row.getCell( COLUMN_ROW_TYPE ) == null ) {
                break; // empty row
            }
            String rowType = getCellValue( row, COLUMN_ROW_TYPE );
            if ( rowType.equalsIgnoreCase( ANSWER_ROW ) ) {
                AnswerVariant answerVariant = new AnswerVariant();
                answerVariant.setText( getCellValue( row, COLUMN_TEXT ) );
                answerVariant.setCorrect( getCellNumber( row, COLUMN_RIGHTNESS ) != null );
                answerVariant.setQuestion( question );
                question.getAnswerVariants().add( answerVariant );
            }
            else {
                break;
            }

        } while ( true );

        this.result = question;
        return rowN;
    }
}
