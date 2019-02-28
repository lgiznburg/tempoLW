package ru.rsmu.tempoLW.components.admin.question;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import ru.rsmu.tempoLW.entities.AnswerVariant;
import ru.rsmu.tempoLW.entities.CorrespondenceVariant;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.entities.QuestionCorrespondence;

/**
 * @author leonid.
 */
public class QuestionCorrespondenceEdit {
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

    @SetupRender
    public void prepare() {
        answersModel = modelFactory.create( getQuestionCorrespondence().getAnswerVariants(), "text" );
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
                for ( AnswerVariant answerVariant : getQuestionCorrespondence().getAnswerVariants() ) {
                    if ( answerVariant.getId() == id ) {
                        return answerVariant;
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
                for ( CorrespondenceVariant variant : getQuestionCorrespondence().getCorrespondenceVariants() ) {
                    if ( variant.getId() == id ) {
                        return variant;
                    }
                }
                return null;
            }
        };
    }

    public QuestionCorrespondence getQuestionCorrespondence() {
        return (QuestionCorrespondence) question;
    }
}
