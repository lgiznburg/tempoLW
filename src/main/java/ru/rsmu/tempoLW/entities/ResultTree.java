package ru.rsmu.tempoLW.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

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

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        if ( !super.equals( o ) ) return false;
        ResultTree that = (ResultTree) o;
        return Objects.equals( correspondenceVariant, that.correspondenceVariant ) &&
                Objects.equals( answerVariant, that.answerVariant );
    }

    @Override
    public int hashCode() {
        return Objects.hash( super.hashCode(), correspondenceVariant, answerVariant );
    }
}
