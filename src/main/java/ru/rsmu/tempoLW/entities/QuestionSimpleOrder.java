package ru.rsmu.tempoLW.entities;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

/**
 * @author leonid.
 */
@Entity
@DiscriminatorValue( "SIMPLE_ORDER" )
public class QuestionSimpleOrder extends Question {
    private static final long serialVersionUID = 8962091025306260043L;

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
        errors += Math.max( 0, correctCount - elements.size() ); // add errors only if there are less answers
        errors += elements.stream().filter( re -> !re.isCorrect() ).count();
        return errors;
    }
}
