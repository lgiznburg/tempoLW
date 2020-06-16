package ru.rsmu.tempoLW.components;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ValueEncoderSource;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
public class QuestionSimpleForm {
    @Property
    private QuestionResult questionResult;

    /**
     * Current exam - stored in the session, used to extract current question from
     */
    @SessionState
    private ExamResult examResult;

    @Property
    private List<AnswerVariant> selectedAnswers;

    @Property
    private SelectModel answerModel;

    @Property
    private boolean plural = false;

    // correct answers counter
    private int count=0;

    @Inject
    private SelectModelFactory modelFactory;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private Messages messages;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    @Inject
    private Request request;

    public void setupRender() {
        prepare();
    }

    public void onPrepareForSubmit() {
        if ( isSessionLost() ) {
            questionResult = new QuestionResult();
            return;
        }
        prepare();
    }

    private void prepare() {
        questionResult = examResult.getCurrentQuestion();

        selectedAnswers = new LinkedList<>();
        if ( questionResult.getElements() != null && questionResult.getElements().size() > 0 ) {
            selectedAnswers.addAll( questionResult.getElements().stream().map( element -> ((ResultSimple) element).getAnswerVariant() ).collect( Collectors.toList() ) );
        }
        // Lazy init
        questionDao.refresh( questionResult.getQuestion() );
        Collections.shuffle( ((QuestionSimple)questionResult.getQuestion()).getAnswerVariants() );
        answerModel = modelFactory.create( ((QuestionSimple)questionResult.getQuestion()).getAnswerVariants(), "text" );
        count = 0;
        for ( AnswerVariant variant : ((QuestionSimple)questionResult.getQuestion()).getAnswerVariants() ) {
            if ( variant.isCorrect() ) {
                count++;
            }
        }
        if ( count > 1 ) {
            plural = true;
        }

    }

    public void onSuccess() {
        if ( isSessionLost() ) return;
        // find correct current question. NB: should it be checked for type?
        questionResult = examResult.getCurrentQuestion();


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
                elementIt.remove();
                if ( element.getId() != 0 ) {
                    // delete element from DB if exists.
                    questionDao.delete( element );
                }
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

        // submit event bubbles up to page level, save result and log there
    }

    public ValueEncoder<AnswerVariant> getAnswerEncoder() {
        return valueEncoderSource.getValueEncoder( AnswerVariant.class );
    }

    public String getMessageWithHint() {
        if ( questionResult.getQuestion().getQuestionInfo().getSubject().isShowAnswersQuantity() ) {
            if ( count < 5 ) {
                return messages.format( "SimpleAnswers-label-hint-2-4", count );
            }
            else {
                return messages.format( "SimpleAnswers-label-hint-ge5", count );
            }
        }
        return messages.get( "SimpleAnswers-label" );
    }

    /**
     * If session expire examResult becomes empty. So we need to show friendly message instead of NPE exception
     * @return true if everything is OK, false if examResult is empty
     */
    private boolean isSessionLost() {
        return request.isXHR() && (examResult == null || examResult.getQuestionResults() == null);
    }
}
