package ru.rsmu.tempoLW.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.QuestionResult;
import ru.rsmu.tempoLW.entities.TestResult;

import java.util.Date;

/**
 * @author leonid.
 */
public class TestFinal {
    @Property
    @SessionState
    private TestResult testResult;

    @Property
    private QuestionResult questionResult;

    @Inject
    private QuestionDao questionDao;

    @Property
    private QuestionResult current;

    public Object onActivate() {
        if ( testResult == null || testResult.getQuestionResults() == null ) {
            return Index.class;
        }
        int finalMark = 0;
        for ( QuestionResult questionResult : testResult.getQuestionResults() ) {
            finalMark += questionResult.getMark();
        }
        testResult.setMarkTotal( finalMark );
        return null;
    }

    public void onActionFromFinish() {
        testResult.setEndTime( new Date() );
    }
}
