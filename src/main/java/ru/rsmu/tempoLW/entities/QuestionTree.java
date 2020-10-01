package ru.rsmu.tempoLW.entities;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

/**
 * @author leonid.
 */
@Entity
@DiscriminatorValue( "TREE" )
public class QuestionTree extends Question {
    private static final long serialVersionUID = 5105975666890792471L;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @LazyCollection( LazyCollectionOption.TRUE )
    private List<CorrespondenceVariant> correspondenceVariants;

    public List<CorrespondenceVariant> getCorrespondenceVariants() {
        return correspondenceVariants;
    }

    public void setCorrespondenceVariants( List<CorrespondenceVariant> correspondenceVariants ) {
        this.correspondenceVariants = correspondenceVariants;
    }

    @Override
    public int countErrors( List<ResultElement> elements ) {
        int correctVariantsCount = 0;
        for ( CorrespondenceVariant correspondenceVariant : correspondenceVariants ) {
            for ( AnswerVariant answerVariant : correspondenceVariant.getCorrectAnswers() ) {
                correctVariantsCount += answerVariant.isCorrect() ? 1 : 0;
            }
        }
        int correctAnswersCount = 0;
        for ( ResultElement element : elements ) {
            correctAnswersCount += element.isCorrect() ? 1 : 0;
        }
        return Math.abs( correctVariantsCount - correctAnswersCount );
    }
}
