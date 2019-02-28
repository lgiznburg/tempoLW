package ru.rsmu.tempoLW.entities;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author leonid.
 */
@Entity
@DiscriminatorValue( "SIMPLE" )
public class ResultSimple extends ResultElement {
    private static final long serialVersionUID = 4225604256734386696L;

    @ManyToOne
    @JoinColumn(name = "answer_variant_id")
    private AnswerVariant answerVariant;

    public AnswerVariant getAnswerVariant() {
        return answerVariant;
    }

    public void setAnswerVariant( AnswerVariant answerVariant ) {
        this.answerVariant = answerVariant;
    }

    @Override
    public void checkCorrectness() {
        setCorrect( answerVariant.isCorrect() );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        if ( !super.equals( o ) ) return false;
        ResultSimple that = (ResultSimple) o;
        return Objects.equals( answerVariant, that.answerVariant );
    }

    @Override
    public int hashCode() {
        return Objects.hash( super.hashCode(), answerVariant );
    }
}
