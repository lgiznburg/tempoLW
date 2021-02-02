package ru.rsmu.tempoLW.utils.builder;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.rsmu.tempoLW.entities.*;

import java.util.LinkedList;
import java.util.List;

/**
 * @author leonid.
 */
public class OpenQuestionBuilder extends QuestionBuilder {
    protected OpenQuestionBuilder() {
    }

    @Override
    public int parse( Row row ) {
        String type = getCellValue( row, COLUMN_QUESTION_TYPE );

        if ( type != null ) {
            // this is question row. should we check question type again?
            try {
                QuestionOpen question = loadQuestion( row, QuestionOpen.class );
                if ( question != null && question.getAnswerVariants() == null ) {
                    question.setAnswerVariants( new LinkedList<>() );
                }
            } catch (IllegalAccessException | InstantiationException e) {
                // constructor did not work. log it
            }
        }
        else if ( result != null ){
            // answer row
            String text = getCellValue( row, COLUMN_TEXT );
            if ( StringUtils.isNotBlank( text ) ) {
                AnswerVariant answerVariant = loadAnswer( row );
                List<AnswerVariant> answers = ((QuestionOpen)result).getAnswerVariants();
                if ( !answers.contains( answerVariant ) ) {
                    answers.add( answerVariant );
                }
            }
        }

        return 0;
    }

    @Override
    public int write( Sheet sheet, int rowN, Question question ) {
        Row row = sheet.createRow( rowN++ );
        writeQuestionInfo( row, question, OPEN_TYPE );

        return writeAnswers( sheet, rowN, ((QuestionOpen)question).getAnswerVariants() );
    }
}
