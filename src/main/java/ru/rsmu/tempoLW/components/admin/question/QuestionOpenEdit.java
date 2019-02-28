package ru.rsmu.tempoLW.components.admin.question;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.tempoLW.entities.AnswerVariant;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.entities.QuestionOpen;

/**
 * @author leonid.
 */
public class QuestionOpenEdit {
    @Parameter( required = true )
    @Property
    private Question question;

    @Property
    private AnswerVariant answerVariant;



    public ValueEncoder<AnswerVariant> getAnswerVariantEncoder() {
        return new ValueEncoder<AnswerVariant>() {
            @Override
            public String toClient( AnswerVariant value ) {
                return String.valueOf( value.getId() );
            }

            @Override
            public AnswerVariant toValue( String clientValue ) {
                Long id = Long.parseLong( clientValue );
                for ( AnswerVariant answerVariant : getQuestionOpen().getAnswerVariants() ) {
                    if ( answerVariant.getId() == id ) {
                        return answerVariant;
                    }
                }
                return null;
            }
        };
    }

    public QuestionOpen getQuestionOpen() {
        return (QuestionOpen) question;
    }
}
