package ru.rsmu.tempoLW.components.admin.question;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.util.AbstractSelectModel;
import ru.rsmu.tempoLW.entities.AnswerVariant;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.entities.QuestionSimpleOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author leonid.
 */
public class QuestionSimpleOrderEdit {
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
                for ( AnswerVariant answerVariant : getQuestionSimpleOrder().getAnswerVariants() ) {
                    if ( answerVariant.getId() == id ) {
                        return answerVariant;
                    }
                }
                return null;
            }
        };
    }

    public QuestionSimpleOrder getQuestionSimpleOrder() {
        return (QuestionSimpleOrder) question;
    }

    public SelectModel getSequenceModel() {
        return new AbstractSelectModel() {
            @Override
            public List<OptionGroupModel> getOptionGroups() {
                return null;
            }

            @Override
            public List<OptionModel> getOptions() {
                List<OptionModel> options = new ArrayList<OptionModel>();
                for ( int i = 0; i <= ((QuestionSimpleOrder)question).getAnswerVariants().size(); i++ ) {
                    options.add(new OptionModelImpl( i==0?"":String.valueOf( i ), i ));
                }
                return options;
            }
        };
    }
}
