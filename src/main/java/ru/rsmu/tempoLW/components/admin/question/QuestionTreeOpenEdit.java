package ru.rsmu.tempoLW.components.admin.question;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ValueEncoderSource;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.pages.admin.question.QuestionList;
import ru.rsmu.tempoLW.pages.admin.question.QuestionView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leonid.
 */
public class QuestionTreeOpenEdit {
    @Parameter( required = true )
    @Property
    private Question question;

    @Property
    private AnswerVariant answerVariant;

    @Property
    private CorrespondenceVariant correspondenceVariant;

    @Property
    private Map<CorrespondenceVariant,String> additionalTextMap;

    @Property
    private String additionalCorrespondenceText;

    private boolean applySubmit = false;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private LinkSource linkSource;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    @InjectPage
    private QuestionList questionList;

    @Inject
    private SelectModelFactory modelFactory;

    @SetupRender
    public void onActivate() {
        prepare();
    }

    public void onPrepareForSubmit() {
        prepare();
    }

    private void prepare() {
        additionalTextMap = new HashMap<>();
        for ( CorrespondenceVariant correspondenceVariant : getQuestionTreeOpen().getCorrespondenceVariants() ) {
            additionalTextMap.put( correspondenceVariant, "" );
        }
    }

    public ValueEncoder<AnswerVariant> getAnswerVariantEncoder() {
        return valueEncoderSource.getValueEncoder( AnswerVariant.class );
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
                for ( CorrespondenceVariant variant : getQuestionTreeOpen().getCorrespondenceVariants() ) {
                    if ( variant.getId() == id ) {
                        return variant;
                    }
                }
                return null;
            }
        };
    }

    public QuestionTreeOpen getQuestionTreeOpen() {
        return (QuestionTreeOpen) question;
    }

    public String getAdditionalAnswerText() {
        return additionalTextMap.get( correspondenceVariant );
    }

    public void setAdditionalAnswerText( String text ) {
        additionalTextMap.put( correspondenceVariant, text );
    }


    public void onDeleteAnswer( AnswerVariant answerVariant ) {
        for ( CorrespondenceVariant variant: getQuestionTreeOpen().getCorrespondenceVariants() ) {
            variant.getCorrectAnswers().remove( answerVariant );
        }
        for ( ResultTree resultTree : questionDao.findResultsOfAnswer( ResultTree.class,  answerVariant ) ) {
            questionDao.delete( resultTree );
        }
        questionDao.delete( answerVariant );
        //questionDao.save( question );
    }

    public void onDeleteVariant( CorrespondenceVariant variant ) {
        //delete results
        for ( AnswerVariant answer : variant.getCorrectAnswers() ) {
            for ( ResultTree resultTree : questionDao.findResultsOfAnswer( ResultTree.class,  answer ) ) {
                questionDao.delete( resultTree );
            }
        }
        // delete answer variants
        List<AnswerVariant> copy = new ArrayList<>( variant.getCorrectAnswers() );
        variant.getCorrectAnswers().clear();
        for ( AnswerVariant answer : copy ) {
            questionDao.delete( answer );
        }
        getQuestionTreeOpen().getCorrespondenceVariants().remove( variant );
        variant.getCorrectAnswers().clear();
        questionDao.delete( variant );
        questionDao.save( question );
    }

    public void onSelectedFromApply() {
        applySubmit = true;
    }

    public Object onSuccess() {
        for ( CorrespondenceVariant correspondence : additionalTextMap.keySet() ) {
            if ( StringUtils.isNotBlank( additionalTextMap.get( correspondence ) ) ) {
                AnswerVariant variant = new AnswerVariant();
                variant.setText( additionalTextMap.get( correspondence ) );
                variant.setQuestion( question );
                correspondence.getCorrectAnswers().add( variant );
                applySubmit = true;
            }
        }
        if ( StringUtils.isNotBlank( additionalCorrespondenceText ) ) {
            CorrespondenceVariant variant = new CorrespondenceVariant();
            variant.setText( additionalCorrespondenceText );
            variant.setQuestion( question );
            getQuestionTreeOpen().getCorrespondenceVariants().add( variant );
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
