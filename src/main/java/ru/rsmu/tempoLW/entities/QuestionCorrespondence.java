package ru.rsmu.tempoLW.entities;

import org.hibernate.annotations.Fetch;
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
        int errors = 0;
        Map<Long,Integer> counts = new HashMap<>();
        for ( ResultElement result : elements ) {
            errors += result.isCorrect() ? 0:1;
            Long id = ((ResultCorrespondence)result).getCorrespondenceVariant().getId();
            if ( counts.containsKey( id ) ) {
                counts.put( id, counts.get( id ) + 1 );
            }
            else {
                counts.put( id, 1 );
            }
        }

        for ( CorrespondenceVariant correspondenceVariant : correspondenceVariants ) {
            Integer size = counts.get( correspondenceVariant.getId() );
            if ( size == null ) size = 0;

            // add errors only if there are less answers
            errors += Math.max( 0, correspondenceVariant.getCorrectAnswers().size() - size );
        }
        return errors;
    }
}
