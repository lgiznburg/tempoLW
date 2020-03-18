package ru.rsmu.tempoLW.components.admin.question;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ValueEncoderSource;
import org.apache.tapestry5.util.AbstractSelectModel;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.pages.admin.question.QuestionList;
import ru.rsmu.tempoLW.pages.admin.question.QuestionView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    @Property
    private String additionalVariantText;

    @Property
    private int additionalVariantOrder;

    private boolean applySubmit = false;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private LinkSource linkSource;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    @InjectPage
    private QuestionList questionList;


    public ValueEncoder<AnswerVariant> getAnswerVariantEncoder() {
        return new ValueEncoder<AnswerVariant>() {
            @Override
            public String toClient( AnswerVariant value ) {
                return String.valueOf( value.getId() );
            }

            @Override
            public AnswerVariant toValue( String clientValue ) {
                long id = Long.parseLong( clientValue );
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
                for ( int i = 0; i <= ((QuestionSimpleOrder)question).getAnswerVariants().size() +1; i++ ) {
                    options.add(new OptionModelImpl( i==0?"":String.valueOf( i ), i ));
                }
                return options;
            }
        };
    }


    public void onDeleteAnswer( AnswerVariant answerVariant ) {
        getQuestionSimpleOrder().getAnswerVariants().remove( answerVariant );
        for ( ResultSimpleOrder resultSimple : questionDao.findResultsOfAnswer( ResultSimpleOrder.class,  answerVariant ) ) {
            questionDao.delete( resultSimple );
        }
        questionDao.delete( answerVariant );
        sortAnswers();
        questionDao.save( question );
    }

    private void sortAnswers() {
        Collections.sort( getQuestionSimpleOrder().getAnswerVariants(),
                new Comparator<AnswerVariant>() {
                    @Override
                    public int compare( AnswerVariant o1, AnswerVariant o2 ) {
                        if ( o1.getSequenceOrder() > 0 ) {
                            return o1.getSequenceOrder() - o2.getSequenceOrder();
                        } else {
                            return o2.getSequenceOrder() == 0 ? 0 : 1;
                        }
                    }
                } );
        int order = 1;
        for ( AnswerVariant answerVariant : getQuestionSimpleOrder().getAnswerVariants() ) {
            if ( answerVariant.getSequenceOrder() > 0 ) {
                answerVariant.setSequenceOrder( order++ );
                answerVariant.setCorrect( true );
            } else {
                answerVariant.setCorrect( false );
            }
        }
    }

    public void onSelectedFromApply() {
        applySubmit = true;
    }

    public Object onSuccess() {
        if ( StringUtils.isNotBlank( additionalVariantText ) ) {
            AnswerVariant variant = new AnswerVariant();
            variant.setText( additionalVariantText );
            variant.setSequenceOrder( additionalVariantOrder );
            variant.setQuestion( question );
            getQuestionSimpleOrder().getAnswerVariants().add( variant );
            applySubmit = true;
        }
        sortAnswers();
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
