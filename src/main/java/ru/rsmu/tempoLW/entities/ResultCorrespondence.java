package ru.rsmu.tempoLW.entities;

import javax.persistence.*;

/**
 * @author leonid.
 */
@Entity
@DiscriminatorValue( "CORRESPONDENCE" )
public class ResultCorrespondence extends ResultElement {
    private static final long serialVersionUID = -2396305700530733143L;

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
        for ( AnswerVariant correctAnswer : correspondenceVariant.getCorrectAnswers() ) {
            if ( correctAnswer.getId() == answerVariant.getId() ) {
                setCorrect( true );
                return;
            }
        }
        setCorrect( false );
    }
}
