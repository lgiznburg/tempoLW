package ru.rsmu.tempoLW.entities;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import ru.rsmu.tempoLW.utils.CorrectnessUtils;

import javax.persistence.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author leonid.
 */
@Entity
@DiscriminatorValue( "TREE_OPEN" )
public class QuestionTreeOpen extends Question {
    private static final long serialVersionUID = -890362280560407939L;

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
        AtomicInteger errors = new AtomicInteger();
        correspondenceVariants.forEach( variant -> {
            ResultTreeOpen myResult = (ResultTreeOpen) elements.stream().filter( el -> ((ResultTreeOpen)el).getCorrespondenceVariant().getId()==variant.getId() )
                    .findFirst().orElse( null );
            if ( myResult == null || StringUtils.isBlank( myResult.getValue() )
                    || CorrectnessUtils.countErrors( myResult.getValue(), variant.getCorrectAnswers() ) > 0 ) {
                // if no answer for current correspondence variant, or answer is empty, or answer is not correct
                // only one increment for entire correspondence variant
                errors.getAndIncrement();
            }
        } );

        return errors.get();
    }
}
