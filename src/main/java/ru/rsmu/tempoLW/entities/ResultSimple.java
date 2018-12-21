package ru.rsmu.tempoLW.entities;

import javax.persistence.*;

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
}
