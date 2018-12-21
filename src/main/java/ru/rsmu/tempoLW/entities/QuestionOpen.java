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

    @Override
    public int countErrors( List<ResultElement> elements ) {
        ResultOpen resultOpen = (ResultOpen) elements.get( 0 );
        if ( resultOpen == null || resultOpen.getValue() == null ) {
            return answerVariants.size();
        }
        String result = resultOpen.getValue().toLowerCase();
        result = result.replaceAll( "(\\d)[\\.](\\d)", "$1,$2" );
        int correctCount = 0;
        for ( AnswerVariant variant : answerVariants ) {
            String[] parts = variant.getText().split( "\\|" );
            for ( String part : parts ) {
                String match = part.trim().toLowerCase().replaceAll( "(\\d)[\\.](\\d)", "$1,$2" );
                if ( result.contains( match ) ) {
                    correctCount++;
                    break;
                }
            }
        }
        return Math.abs( correctCount - answerVariants.size() ) ;
    }
}
