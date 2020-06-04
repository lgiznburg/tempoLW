package ru.rsmu.tempoLW.components;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ValueEncoderSource;
import org.apache.tapestry5.util.AbstractSelectModel;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author leonid.
 */
public class QuestionSimpleOrderForm {
    @Property
    private QuestionResult questionResult;

    /**
     * Current exam - stored in the session, used to extract current question from
     */
    @SessionState
    private ExamResult examResult;

    @Property
    private List<ResultSimpleOrder> resultElements;

    @Property
    private ResultSimpleOrder resultElement;

    @Property
    private SelectModel sequenceModel;

    private int count=0;

    @Inject
    private SelectModelFactory modelFactory;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private Messages messages;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    public void setupRender() {
        prepare();
    }

    public void onPrepareForSubmit() {
        prepare();
    }


    private void prepare() {
        questionResult = examResult.getCurrentQuestion();

        // Lazy init
        questionDao.refresh( questionResult.getQuestion() );
        // create results for displaying on the form
        resultElements = new LinkedList<>();
        QuestionSimpleOrder question = (QuestionSimpleOrder)questionResult.getQuestion();
        for ( AnswerVariant variant : question.getAnswerVariants() ) {
            ResultSimpleOrder element = new ResultSimpleOrder();
            element.setAnswerVariant( variant );
            // copy previous selection
            if ( questionResult.getElements() != null && !questionResult.getElements().isEmpty() ) {
                ResultSimpleOrder existed = findElementForAnswer( variant );
                if ( existed != null ) {
                    element.setEnteredOrder( existed.getEnteredOrder() );
                }
            }
            resultElements.add( element );
            if ( variant.getSequenceOrder() > 0 ) {
                count++;
            }
        }
        Collections.shuffle( resultElements );

        sequenceModel = new AbstractSelectModel() {
            @Override
            public List<OptionGroupModel> getOptionGroups() {
                return null;
            }

            @Override
            public List<OptionModel> getOptions() {
                List<OptionModel> options = new ArrayList<OptionModel>();
                for ( int i = 0; i <= question.getAnswerVariants().size(); i++ ) {
                    options.add(new OptionModelImpl( i==0? "—":String.valueOf( i ), i )); //messages.get( "incorrect_answer" )
                }
                return options;
            }
        };

    }

    public void onSuccess() {
        // find correct current question. NB: should it be checked for type?
        questionResult = examResult.getCurrentQuestion();

        // create elements if not exist
        if ( questionResult.getElements() == null ) {
            questionResult.setElements( new LinkedList<>() );
        }
        for ( ResultSimpleOrder element : resultElements ) {
            ResultSimpleOrder existed = findElementForAnswer( element.getAnswerVariant() );
            if ( element.getEnteredOrder() > 0 ) {  // answer was selected as correct
                if ( existed != null ) {
                    // copy new value
                    existed.setEnteredOrder( element.getEnteredOrder() );
                }
                else { // add result to question result
                    element.setQuestionResult( questionResult );
                    questionResult.getElements().add( element );
                }
            }
            else { // answer was selected as incorrect
                if ( existed != null ) {
                    // remove from DB (use DAO)
                    questionResult.getElements().remove( existed );
                    if ( existed.getId() > 0 ) {
                        questionDao.delete( existed );
                    }
                }
            }
        }
    }

    protected ResultSimpleOrder findElementForAnswer( AnswerVariant variant ) {
        for ( ResultElement element : questionResult.getElements() ) {
            if ( element instanceof ResultSimpleOrder ) {
                if ( ((ResultSimpleOrder)element).getAnswerVariant().getId() == variant.getId() ) {
                    return (ResultSimpleOrder) element;
                }
            }
        }
        return null;
    }

    public ValueEncoder<ResultSimpleOrder> getResultEncoder() {
        return new ValueEncoder<ResultSimpleOrder>() {
            @Override
            public String toClient( ResultSimpleOrder value ) {
                return String.valueOf( value.getAnswerVariant().getId() );
            }

            @Override
            public ResultSimpleOrder toValue( String clientValue ) {
                long id = Long.parseLong( clientValue );
                for ( ResultSimpleOrder element : resultElements ) {
                    if ( element.getAnswerVariant().getId() == id ) {
                        return element;
                    }
                }
                return null;
            }
        };
    }

    public String getMessageWithHint() {
        if ( questionResult.getQuestion().getQuestionInfo().getSubject().isShowAnswersQuantity() ) {
            if ( count < 5 ) {
                return messages.format( "SimpleOrderAnswer-label-hint-lt5", count );
            }
            else {
                return messages.format( "SimpleOrderAnswer-label-hint-ge5", count );
            }
        }
        return messages.get( "SimpleOrderAnswer-label" );
    }
}
