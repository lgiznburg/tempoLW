package ru.rsmu.tempoLW.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

/**
 * This question intends for creating olympiads. It has no predefined answers and
 * testee should upload solution in form of PDF or image
 * @author leonid.
 */
@Entity
@DiscriminatorValue( "FREE" )
public class QuestionFree extends Question {

    private static final long serialVersionUID = -3414568066454724872L;

    @Override
    /** This question is not intended for automatic checking,
     * so it's return max number of error to set question mark to 0
     */
    public int countErrors( List<ResultElement> elements ) {
        return getQuestionInfo().getMaxScore();
    }
}
