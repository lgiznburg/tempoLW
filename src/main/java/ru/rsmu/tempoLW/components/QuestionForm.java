package ru.rsmu.tempoLW.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.tempoLW.entities.*;

/**
 * @author leonid.
 */
public class QuestionForm {

    @Parameter(required = true)
    @Property
    private QuestionResult current;

    public boolean isQuestionSimple() {
        return current.getQuestion() instanceof QuestionSimple;
    }

    public boolean isQuestionOpen() {
        return current.getQuestion() instanceof QuestionOpen;
    }

    public boolean isQuestionCorrespondence() {
        return current.getQuestion() instanceof QuestionCorrespondence;
    }

   public boolean isQuestionSimpleOrder() {
        return current.getQuestion() instanceof QuestionSimpleOrder;
    }

    public int getReadableNumber() {
        return current.getOrderNumber() + 1;
    }

}
