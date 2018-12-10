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
@DiscriminatorValue( "OPEN" )
public class QuestionOpen extends Question {
    private static final long serialVersionUID = -8942060514891282894L;

    @OneToMany( mappedBy = "question", cascade = CascadeType.ALL )
    List<AnswerVariant> answerVariants;

    public List<AnswerVariant> getAnswerVariants() {
        return answerVariants;
    }

    public void setAnswerVariants( List<AnswerVariant> answerVariants ) {
        this.answerVariants = answerVariants;
    }
}
