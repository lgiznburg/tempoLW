package ru.rsmu.tempoLW.components.admin.question;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import ru.rsmu.tempoLW.entities.AnswerVariant;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.entities.QuestionSimple;

/**
 * @author leonid.
 */
public class QuestionSimpleEdit {
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
                for ( AnswerVariant answerVariant : getQuestionSimple().getAnswerVariants() ) {
                    if ( answerVariant.getId() == id ) {
                        return answerVariant;
                    }
                }
                return null;
            }
        };
    }

    public QuestionSimple getQuestionSimple() {
        return (QuestionSimple) question;
    }
}
