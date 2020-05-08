package ru.rsmu.tempoLW.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.QuestionResult;
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

    public void onPrepareForRender() {
        prepare();
    }

    public void onPrepareForSubmit() {
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
        // find correct current question. NB: should it be checked for type?
        questionResult = examResult.getCurrentQuestion();

        questionDao.refresh( questionResult.getQuestion() );
        if ( questionResult.getElements() == null ) {
            questionResult.setElements( new LinkedList<>() );
        }
        if ( questionResult.getElements().size() == 0 ) {
            questionResult.getElements().add( resultOpen );
        }
    }
}
