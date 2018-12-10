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
}
