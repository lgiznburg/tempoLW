package ru.rsmu.tempoLW.components;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.QuestionResult;
import ru.rsmu.tempoLW.entities.ResultElement;
import ru.rsmu.tempoLW.entities.ResultOpen;

import java.util.LinkedList;

/**
 * @author leonid.
 */
public class QuestionOpenForm {
    @Property
    private QuestionResult questionResult;

    /**
     * Current exam - stored in the session, used to extract current question from
     */
    @SessionState
    private ExamResult examResult;

    @Property
    private ResultOpen resultOpen;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private Request request;

    public void onPrepareForRender() {
        prepare();
    }

    public void onPrepareForSubmit() {
        if ( !checkSessionIntegrity() ) return;
        prepare();
    }

    private void prepare() {
        questionResult = examResult.getCurrentQuestion();

        if ( questionResult.getElements() != null && questionResult.getElements().size() > 0 ) {
            resultOpen = (ResultOpen) questionResult.getElements().get( 0 );
        }
        else {
            resultOpen = new ResultOpen();
            resultOpen.setQuestionResult( questionResult );
        }
    }

    public void onSuccess() {
        if ( !checkSessionIntegrity() ) return;
        // find correct current question. NB: should it be checked for type?
        questionResult = examResult.getCurrentQuestion();

        questionDao.refresh( questionResult.getQuestion() );
        if ( questionResult.getElements() == null ) {
            questionResult.setElements( new LinkedList<>() );
        }
        if ( StringUtils.isBlank( resultOpen.getValue() ) ) {  // empty value => no answer
            if ( questionResult.getElements().size() > 0 ) {
                // could be only 1 answer
                ResultElement existed = questionResult.getElements().get( 0 );
                questionResult.getElements().remove( 0 );
                if ( existed.getId() > 0 ) {
                    questionDao.delete( existed );
                }
            }
        }
        else if ( questionResult.getElements().size() == 0 ) {
            questionResult.getElements().add( resultOpen );
        }
    }

    /**
     * If session expire examResult becomes empty. So we need to show friendly message instead of NPE exception
     * @return true if everything is OK, false if examResult is empty
     */
    private boolean checkSessionIntegrity() {
        if ( request.isXHR() && ( examResult == null || examResult.getQuestionResults() == null ) ) {
            return false;
        }
        return true;
    }
}
