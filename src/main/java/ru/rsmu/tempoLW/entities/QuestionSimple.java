package ru.rsmu.tempoLW.entities;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * @author leonid.
 */
@Entity
@DiscriminatorValue( "SIMPLE" )
public class QuestionSimple extends Question {
    private static final long serialVersionUID = -7970692189259663030L;

    @OneToMany( mappedBy = "question", cascade = CascadeType.ALL )
    List<AnswerVariant> answerVariants;

    public List<AnswerVariant> getAnswerVariants() {
        return answerVariants;
    }

    public void setAnswerVariants( List<AnswerVariant> answerVariants ) {
        this.answerVariants = answerVariants;
    }

    @Override
    public int countErrors( List<ResultElement> elements ) {
        int errors = 0;
        int correctCount = 0;
        for ( AnswerVariant answerVariant : answerVariants ) {
            correctCount += answerVariant.isCorrect() ? 1 : 0;
        }
        errors += Math.max( 0, correctCount - elements.size() ); // add errors only if there are less answers
        for ( ResultElement result : elements ) {
            errors += result.isCorrect() ? 0 : 1;
        }
        return errors;
    }
}
