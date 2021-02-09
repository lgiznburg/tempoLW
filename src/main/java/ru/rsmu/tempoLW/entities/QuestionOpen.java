package ru.rsmu.tempoLW.entities;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import ru.rsmu.tempoLW.utils.CorrectnessUtils;

import javax.persistence.*;
import java.util.List;

/**
 * @author leonid.
 */
@Entity
@DiscriminatorValue( "OPEN" )
public class QuestionOpen extends Question {
    private static final long serialVersionUID = -8942060514891282894L;

    @OneToMany( mappedBy = "question", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @LazyCollection( LazyCollectionOption.TRUE )
    private List<AnswerVariant> answerVariants;

    public List<AnswerVariant> getAnswerVariants() {
        return answerVariants;
    }

    public void setAnswerVariants( List<AnswerVariant> answerVariants ) {
        this.answerVariants = answerVariants;
    }

    @Override
    public int countErrors( List<ResultElement> elements ) {
        ResultOpen resultOpen = (ResultOpen) elements.get( 0 );
        if ( resultOpen == null || resultOpen.getValue() == null ) {
            return answerVariants.size();
        }
        return CorrectnessUtils.countErrors( resultOpen.getValue(), answerVariants );
    }
}
