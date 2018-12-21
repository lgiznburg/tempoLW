package ru.rsmu.tempoLW.components;

import org.apache.tapestry5.annotations.Parameter;
import ru.rsmu.tempoLW.entities.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author leonid.
 */
public class QuestionPreview {

    @Parameter(required = true)
    private Question question;

    private AnswerVariant answer;

    private CorrespondenceVariant correspondence;

    public Question getQuestion() {
        return question;
    }

    public void setQuestion( Question question ) {
        this.question = question;
    }

    public AnswerVariant getAnswer() {
        return answer;
    }

    public void setAnswer( AnswerVariant answer ) {
        this.answer = answer;
    }

    public CorrespondenceVariant getCorrespondence() {
        return correspondence;
    }

    public void setCorrespondence( CorrespondenceVariant correspondence ) {
        this.correspondence = correspondence;
    }

    public List<AnswerVariant> getSimpleAnswers() {
        if ( question instanceof QuestionSimple ) {
            return ((QuestionSimple)question).getAnswerVariants();
        }
        else if ( question instanceof QuestionOpen ) {
            return ((QuestionOpen)question).getAnswerVariants();
        }
        else if ( question instanceof QuestionCorrespondence ) {
            List<AnswerVariant> answerVariants = new LinkedList<>();
            for ( AnswerVariant variant : ((QuestionCorrespondence) question).getAnswerVariants() ) {
                if ( !variant.isCorrect() ) {
                    answerVariants.add( variant );
                }
            }
            return answerVariants;
        }

        return Collections.emptyList();
    }

    public List<CorrespondenceVariant> getCorrespondenceVariants() {
        if ( question instanceof QuestionCorrespondence ) {
            return ((QuestionCorrespondence)question).getCorrespondenceVariants();
        }
        return Collections.emptyList();
    }

    public boolean isQuestionSimple() {
        return question instanceof QuestionSimple;
    }

    public boolean isQuestionOpen() {
        return question instanceof QuestionOpen;
    }

    public boolean isQuestionCorrespondence() {
        return question instanceof QuestionCorrespondence;
    }

}
