package ru.rsmu.tempoLW.components.admin.question;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import ru.rsmu.tempoLW.entities.*;

import java.util.LinkedList;
import java.util.List;

/**
 * @author leonid.
 */
public class QuestionTreeEdit {
    @Parameter( required = true )
    @Property
    private Question question;

    @Property
    private AnswerVariant answerVariant;

    @Property
    private CorrespondenceVariant correspondenceVariant;

    @Inject
    private SelectModelFactory modelFactory;

    @Property
    private SelectModel answersModel;

    //@Property
    //private List<AnswerVariant> allAnswers;

    @SetupRender
    public void onActivate() {
        //allAnswers = new LinkedList<>();
        //getQuestionTree().getCorrespondenceVariants().forEach( correspondenceVariant -> allAnswers.addAll( correspondenceVariant.getCorrectAnswers() ) );
        //answersModel = modelFactory.create( allAnswers, "text" );
    }

    public ValueEncoder<AnswerVariant> getAnswerVariantEncoder() {
        return new ValueEncoder<AnswerVariant>() {
            @Override
            public String toClient( AnswerVariant value ) {
                return String.valueOf( value.getId() );
            }

            @Override
            public AnswerVariant toValue( String clientValue ) {
                long id = Long.parseLong( clientValue );
                for ( CorrespondenceVariant variant : ((QuestionTree)question).getCorrespondenceVariants() ) {
                    for ( AnswerVariant answerVariant : variant.getCorrectAnswers() ) {
                        if ( answerVariant.getId() == id ) {
                            return answerVariant;
                        }
                    }
                }
                return null;
            }
        };
    }

    public ValueEncoder<CorrespondenceVariant> getCorrespondenceVariantEncoder() {
        return new ValueEncoder<CorrespondenceVariant>() {
            @Override
            public String toClient( CorrespondenceVariant value ) {
                return String.valueOf( value.getId() );
            }

            @Override
            public CorrespondenceVariant toValue( String clientValue ) {
                long id = Long.parseLong( clientValue );
                for ( CorrespondenceVariant variant : getQuestionTree().getCorrespondenceVariants() ) {
                    if ( variant.getId() == id ) {
                        return variant;
                    }
                }
                return null;
            }
        };
    }

    public QuestionTree getQuestionTree() {
        return (QuestionTree) question;
    }
}
