package ru.rsmu.tempoLW.components;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
public class QuestionSimpleForm {
    @Parameter(required = true)
    @Property
    private QuestionResult questionResult;

    @Property
    private List<AnswerVariant> selectedAnswers;

    @Property
    private SelectModel answerModel;

    @Property
    private boolean plural = false;

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
        selectedAnswers = new LinkedList<>();
        if ( questionResult.getElements() != null && questionResult.getElements().size() > 0 ) {
            selectedAnswers.addAll( questionResult.getElements().stream().map( element -> ((ResultSimple) element).getAnswerVariant() ).collect( Collectors.toList() ) );
        }
        // Lazy init
        questionDao.refresh( questionResult.getQuestion() );
        Collections.shuffle( ((QuestionSimple)questionResult.getQuestion()).getAnswerVariants() );
        answerModel = modelFactory.create( ((QuestionSimple)questionResult.getQuestion()).getAnswerVariants(), "text" );
        int count = 0;
        for ( AnswerVariant variant : ((QuestionSimple)questionResult.getQuestion()).getAnswerVariants() ) {
            if ( variant.isCorrect() ) {
                count++;
            }
            if ( count > 1 ) {
                plural = true;
                break;
            }
        }
    }

    public void onSuccess() {
        // create elements if not exist
        if ( questionResult.getElements() == null ) {
            questionResult.setElements( new LinkedList<>() );
        }
        // collect existed results for question result
        List<AnswerVariant> existedAnswers = new LinkedList<>();
        existedAnswers.addAll( questionResult.getElements().stream().map( element -> ((ResultSimple) element).getAnswerVariant() ).collect( Collectors.toList() )  );
        //check if existed result is still checked, and remove it if it's needed
        for ( Iterator<ResultElement> elementIt = questionResult.getElements().iterator(); elementIt.hasNext(); ) {
            ResultElement element = elementIt.next();
            if ( !selectedAnswers.contains( ((ResultSimple)element).getAnswerVariant() ) ) {
                if ( element.getId() != 0 ) {
                    // delete element from DB if exists.
                    questionDao.delete( element );
                }
                elementIt.remove();
            }
        }
        // add checked answer to result list
        for ( AnswerVariant variant : selectedAnswers ) {
            if ( !existedAnswers.contains( variant ) ) {
                ResultSimple resultSimple = new ResultSimple();
                resultSimple.setQuestionResult( questionResult );
                resultSimple.setAnswerVariant( variant );
                questionResult.getElements().add( resultSimple );
            }
        }

        //TODO save result and log
    }

    public ValueEncoder<AnswerVariant> getAnswerEncoder() {
        return new LocalAnswerVariantEncoder();
    }

    public class LocalAnswerVariantEncoder implements ValueEncoder<AnswerVariant> {

        @Override
        public String toClient( AnswerVariant value ) {
            return String.valueOf( value.getId() );
        }

        @Override
        public AnswerVariant toValue( String clientValue ) {
            long id = Long.parseLong( clientValue );
            for ( AnswerVariant variant : ( (QuestionSimple) questionResult.getQuestion() ).getAnswerVariants() ) {
                if ( variant.getId() == id ) {
                    return variant;
                }
            }
            return null;
        }
    }
}
