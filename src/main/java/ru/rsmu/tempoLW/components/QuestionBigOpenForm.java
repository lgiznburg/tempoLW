package ru.rsmu.tempoLW.components;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;

import java.util.LinkedList;

/**
 * @author leonid.
 */
public class QuestionBigOpenForm {
    @Property
    private QuestionResult questionResult;

    /**
     * Current exam - stored in the session, used to extract current question from
     */
    @SessionState
    private ExamResult examResult;

    @Property
    private ResultBigOpen resultBigOpen;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private Request request;

    public void onPrepareForRender() {
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

        if ( questionResult.getElements() != null && questionResult.getElements().size() > 0 ) {
            resultBigOpen = (ResultBigOpen) questionResult.getElements().get( 0 );
        }
        else {
            resultBigOpen = new ResultBigOpen();
            resultBigOpen.setText( new AttachedLongText() );
            resultBigOpen.getText().setResultElement( resultBigOpen );
            resultBigOpen.setQuestionResult( questionResult );
        }
    }

    public void onSuccess() {
        if ( isSessionLost() ) return;
        // find correct current question. NB: should it be checked for type?
        questionResult = examResult.getCurrentQuestion();

        questionDao.refresh( questionResult.getQuestion() );
        if ( questionResult.getElements() == null ) {
            questionResult.setElements( new LinkedList<>() );
        }
        if ( StringUtils.isBlank( resultBigOpen.getText().getText() ) ) {  // empty value => no answer
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
            questionResult.getElements().add( resultBigOpen );
        }
    }

    /**
     * If session expire examResult becomes empty. So we need to show friendly message instead of NPE exception
     * @return true if everything is OK, false if examResult is empty
     */
    private boolean isSessionLost() {
        return request.isXHR() && (examResult == null || examResult.getQuestionResults() == null);
    }

    public boolean isExamMode() {
        return examResult.isExamMode();
    }
}
