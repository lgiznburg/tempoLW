package ru.rsmu.tempoLW.entities;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leonid.
 */
@Entity
@DiscriminatorValue( "CORRESPONDENCE" )
public class QuestionCorrespondence extends Question {
    private static final long serialVersionUID = 2586460208558792514L;

    @OneToMany( mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @LazyCollection( LazyCollectionOption.TRUE )
    private List<AnswerVariant> answerVariants;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @LazyCollection( LazyCollectionOption.TRUE )
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

    @Override
    public int countErrors( List<ResultElement> elements ) {
        Map<Long,Integer> counts = new HashMap<>();  // map of variant ID -> number of given answers
        //1. Count correct answers
        int correctCount = (int) elements.stream().filter( ResultElement::isCorrect ).count();
        //2. all other answers are incorrect^ count errors
        int errors = elements.size() - correctCount;

        //3. add "missing" answers, if there is less answers for each variant than it should be
        for ( CorrespondenceVariant correspondenceVariant : correspondenceVariants ) {
            int givenAnswersCount = (int) elements.stream()
                    .filter( re -> ((ResultCorrespondence)re).getCorrespondenceVariant().getId() == correspondenceVariant.getId() )
                    .count();

            // add errors only if there are less answers
            errors += Math.max( 0, correspondenceVariant.getCorrectAnswers().size() - givenAnswersCount );
        }
        return errors;
    }
}
