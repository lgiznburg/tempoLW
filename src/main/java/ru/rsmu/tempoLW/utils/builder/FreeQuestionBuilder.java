package ru.rsmu.tempoLW.utils.builder;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.entities.QuestionFree;

/**
 * @author leonid.
 */
public class FreeQuestionBuilder extends QuestionBuilder {
    protected FreeQuestionBuilder() {}

    @Override
    public int parse( Row row ) {

        String type = getCellValue( row, COLUMN_QUESTION_TYPE );

        if ( type != null ) {
            // this is question row. should we check question type again?
            try {
                QuestionFree question = loadQuestion( row, QuestionFree.class );
            } catch (IllegalAccessException | InstantiationException e) {
                // constructor did not work. log it
            }
        }

        return 0;
    }

    @Override
    public int write( Sheet sheet, int rowN, Question question ) {
        Row row = sheet.createRow( rowN++ );
        writeQuestionInfo( row, question, FREE_TYPE );

        return rowN;
    }
}
