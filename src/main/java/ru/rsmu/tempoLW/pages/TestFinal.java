package ru.rsmu.tempoLW.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.QuestionResult;
import ru.rsmu.tempoLW.entities.ExamResult;

import java.util.Date;

/**
 * @author leonid.
 */
public class TestFinal {
    @Property
    @SessionState
    private ExamResult examResult;

    @Property
    private QuestionResult questionResult;

    @Inject
    private QuestionDao questionDao;

    @Property
    private QuestionResult current;

    public Object onActivate() {
        if ( examResult == null || examResult.getQuestionResults() == null ) {
            return Index.class;
        }
        int finalMark = 0;
        for ( QuestionResult questionResult : examResult.getQuestionResults() ) {
            finalMark += questionResult.getMark();
        }
        examResult.setMarkTotal( finalMark );
        return null;
    }

    public void onActionFromFinish() {
        examResult.setEndTime( new Date() );
    }
}
