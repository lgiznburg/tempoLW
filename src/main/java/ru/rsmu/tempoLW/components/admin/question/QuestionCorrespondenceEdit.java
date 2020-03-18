package ru.rsmu.tempoLW.components.admin.question;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.SelectModel;
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

    @Property
    private String additionalVariantText;

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


    public void onDeleteAnswer( AnswerVariant answerVariant ) {
        getQuestionCorrespondence().getAnswerVariants().remove( answerVariant );
        for ( ResultCorrespondence resultCorrespondence : questionDao.findResultsOfAnswer( ResultCorrespondence.class,  answerVariant ) ) {
            questionDao.delete( resultCorrespondence );
        }
        for ( CorrespondenceVariant correspondenceVariant : getQuestionCorrespondence().getCorrespondenceVariants() ) {
            correspondenceVariant.getCorrectAnswers().remove( answerVariant );
        }
        questionDao.delete( answerVariant );
        questionDao.save( question );
    }

    public void onDeleteVariant( CorrespondenceVariant variant ) {
        for ( AnswerVariant answer : getQuestionCorrespondence().getAnswerVariants() ) {
            for ( ResultCorrespondence resultCorrespondence : questionDao.findResultsOfAnswer( ResultCorrespondence.class,  answer ) ) {
                if ( resultCorrespondence.getCorrespondenceVariant().equals( variant ) ) {
                    questionDao.delete( resultCorrespondence );
                }
            }
        }
        getQuestionCorrespondence().getCorrespondenceVariants().remove( variant );
        questionDao.save( question );
        variant.getCorrectAnswers().clear();
        questionDao.delete( variant );
    }

    public void onSelectedFromApply() {
        applySubmit = true;
    }

    public Object onSuccess() {
        if ( StringUtils.isNotBlank( additionalVariantText ) ) {
            AnswerVariant variant = new AnswerVariant();
            variant.setText( additionalVariantText );
            variant.setQuestion( question );
            getQuestionCorrespondence().getAnswerVariants().add( variant );
            applySubmit = true;
        }
        if ( StringUtils.isNotBlank( additionalCorrespondenceText ) ) {
            CorrespondenceVariant variant = new CorrespondenceVariant();
            variant.setText( additionalCorrespondenceText );
            variant.setQuestion( question );
            getQuestionCorrespondence().getCorrespondenceVariants().add( variant );
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
