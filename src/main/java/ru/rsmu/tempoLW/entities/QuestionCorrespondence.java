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
@DiscriminatorValue( "CORRESPONDENCE" )
public class QuestionCorrespondence extends Question {
    private static final long serialVersionUID = 2586460208558792514L;

    @OneToMany( mappedBy = "question", cascade = CascadeType.ALL )
    private List<AnswerVariant> answerVariants;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL )
    private List<CorrespondenceVariant> correspondenceVariants;

    public List<AnswerVariant> getAnswerVariants() {
        return answerVariants;
    }

    public void setAnswerVariants( List<AnswerVariant> answerVariants ) {
        this.answerVariants = answerVariants;
    }

    public List<CorrespondenceVariant> getCorrespondenceVariants() {
        return correspondenceVariants;
    }

    public void setCorrespondenceVariants( List<CorrespondenceVariant> correspondenceVariants ) {
        this.correspondenceVariants = correspondenceVariants;
    }
}
