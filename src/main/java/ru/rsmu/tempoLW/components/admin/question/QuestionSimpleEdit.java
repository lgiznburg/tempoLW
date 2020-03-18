package ru.rsmu.tempoLW.components.admin.question;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ValueEncoderSource;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.pages.admin.question.QuestionList;
import ru.rsmu.tempoLW.pages.admin.question.QuestionView;

/**
 * @author leonid.
 */
public class QuestionSimpleEdit {
    @Parameter( required = true )
    @Property
    private Question question;

    @Property
    private AnswerVariant answerVariant;

    @Property
    private String additionalVariantText;

    @Property
    private boolean additionalVariantCorrectness;

    private boolean applySubmit = false;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private LinkSource linkSource;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    @InjectPage
    private QuestionList questionList;

    /*void setupRender() {
        additionalVariant = new AnswerVariant();
    }

    void onPrepareForSubmit() {
        additionalVariant = new AnswerVariant();
    }*/

    public ValueEncoder<AnswerVariant> getAnswerVariantEncoder() {
        return new ValueEncoder<AnswerVariant>() {
            @Override
            public String toClient( AnswerVariant value ) {
                return String.valueOf( value.getId() );
            }

            @Override
            public AnswerVariant toValue( String clientValue ) {
                long id = Long.parseLong( clientValue );
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

    public void onDeleteAnswer( AnswerVariant answerVariant ) {
        getQuestionSimple().getAnswerVariants().remove( answerVariant );
        for ( ResultSimple resultSimple : questionDao.findResultsOfAnswer( ResultSimple.class,  answerVariant ) ) {
            questionDao.delete( resultSimple );
        }
        questionDao.delete( answerVariant );
        questionDao.save( question );
    }

    public void onSelectedFromApply() {
        applySubmit = true;
    }

    public Object onSuccess() {
        if ( StringUtils.isNotBlank( additionalVariantText ) ) {
            AnswerVariant variant = new AnswerVariant();
            variant.setText( additionalVariantText );
            variant.setCorrect( additionalVariantCorrectness );
            variant.setQuestion( question );
            getQuestionSimple().getAnswerVariants().add( variant );
            applySubmit = true;
        }
        questionDao.save( question );
        if ( applySubmit ) {
            return null;
        }
        return linkSource.createPageRenderLink( "admin/question/" + QuestionView.class.getSimpleName(), false, question );
    }

    public Object onToQuestionList() {
        questionList.set( question.getQuestionInfo().getSubject() );
        return questionList;
    }

}
