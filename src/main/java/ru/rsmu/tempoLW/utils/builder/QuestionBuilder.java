package ru.rsmu.tempoLW.utils.builder;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.entities.QuestionInfo;
import ru.rsmu.tempoLW.entities.TestSubject;
import ru.rsmu.tempoLW.utils.ExcelLayout;

/**
 * @author leonid.
 */
public abstract class QuestionBuilder extends ExcelLayout {

    protected Question result;


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
        throw new IllegalArgumentException( "Incorrect type of question" );
    }

    public abstract int parse( Sheet sheet, int rowN );

    public Question getResult() {
        return result;
    }
}
