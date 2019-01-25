package ru.rsmu.tempoLW.components;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
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
    @Parameter(required = true)
    @Property
    private QuestionResult questionResult;

    @Property
    private List<ResultSimpleOrder> resultElements;

    @Property
    private ResultSimpleOrder resultElement;

    @Property
    private SelectModel sequenceModel;

    @Inject
    private SelectModelFactory modelFactory;

    @Inject
    private QuestionDao questionDao;


    public void setupRender() {
        prepare();
    }

    public void onPrepareForSubmit() {
        prepare();
    }


    private void prepare() {
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
                    options.add(new OptionModelImpl( i==0?"":String.valueOf( i ), i ));
                }
                return options;
            }
        };

    }

    public void onSuccess() {
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
                    //todo remove from DB (use DAO)
                    questionResult.getElements().remove( existed );
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
}
