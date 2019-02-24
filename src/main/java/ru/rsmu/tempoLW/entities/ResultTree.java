package ru.rsmu.tempoLW.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author leonid.
 */
@Entity
@DiscriminatorValue( "TREE" )
public class ResultTree extends ResultElement {
    private static final long serialVersionUID = 6896909165673060522L;

    @ManyToOne
    @JoinColumn(name = "correspondence_variant_id")
    private CorrespondenceVariant correspondenceVariant;

    @ManyToOne
    @JoinColumn(name = "answer_variant_id")
    private AnswerVariant answerVariant;

    public CorrespondenceVariant getCorrespondenceVariant() {
        return correspondenceVariant;
    }

    public void setCorrespondenceVariant( CorrespondenceVariant correspondenceVariant ) {
        this.correspondenceVariant = correspondenceVariant;
    }

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
