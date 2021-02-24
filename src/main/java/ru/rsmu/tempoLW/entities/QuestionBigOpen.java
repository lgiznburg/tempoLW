package ru.rsmu.tempoLW.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

/**
 * @author leonid.
 */
@Entity
@DiscriminatorValue( "BIG_OPEN" )
public class QuestionBigOpen extends Question {
    private static final long serialVersionUID = 770318946370316307L;

    /** This question is not intended for automatic checking,
     * so it's return max number of error to set question mark to 0
     */
    @Override
    public int countErrors( List<ResultElement> elements ) {
        return getQuestionInfo().getMaxScore();
    }

    @Override
    public boolean isManualCheckingRequired() {
        return true;
    }
}
