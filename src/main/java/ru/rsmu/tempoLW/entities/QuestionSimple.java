package ru.rsmu.tempoLW.entities;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

/**
 * @author leonid.
 */
@Entity
@DiscriminatorValue( "SIMPLE" )
public class QuestionSimple extends Question {
    private static final long serialVersionUID = -7970692189259663030L;

    @OneToMany( mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @LazyCollection( LazyCollectionOption.TRUE )
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
        int correctCount = (int) answerVariants.stream().filter( AnswerVariant::isCorrect ).count();
        int correctEntered = (int) elements.stream().filter( ResultElement::isCorrect ).count();
        errors += Math.max( 0, correctCount - correctEntered ); // add errors only if there are less answers
        errors += (int) elements.stream().filter( rs -> !rs.isCorrect() ).count(); //count incorrect results
        return errors;
    }
}
