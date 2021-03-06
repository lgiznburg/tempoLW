package ru.rsmu.tempoLW.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.tynamo.security.services.SecurityService;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.QuestionResult;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.auth.UserRoleName;
import ru.rsmu.tempoLW.services.SecurityUserHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    @Inject
    private ExamDao examDao;

    @Property
    private QuestionResult current;

    @Inject
    private SecurityService securityService;


    public Object onActivate() {
        if ( examResult == null || examResult.getQuestionResults() == null ) {
            return Index.class;
        }
        if ( examResult.getId() > 0 ) {
            //proof lazy init exception
            if ( !Hibernate.isInitialized( examResult.getQuestionResults() ) ) {
                examDao.refresh( examResult );
            }
        }
        int finalMark = 0;
        for ( QuestionResult questionResult : examResult.getQuestionResults() ) {
            finalMark += questionResult.getMark();
        }
        examResult.setMarkTotal( finalMark );

        if ( examResult.isExamMode() && getEstimatedEndTime().before( new Date() ) ) {
            // check time - if testee used "Next/Prev question" button
            examResult.setEndTime( new Date() );
            examDao.save( examResult );
        }
        return null;
    }

    public void onFinishTest() {
        examResult.setEndTime( new Date() );
        //save only existed result
        if ( examResult.getId() > 0 ) {
            examDao.save( examResult );
        }
    }

    public Object onGoBack()
    {
        if ( securityService.isUser() && securityService.hasRole( UserRoleName.testee.name() ) ) {
            securityService.getSubject().logout();
        }
        return Index.class;
    }


    public Date getEstimatedEndTime() {
        Calendar calendar = Calendar.getInstance();
        if ( examResult.isExamMode() ) {
            calendar.setTime( examResult.getStartTime() != null ? examResult.getStartTime() : new Date() );
            if ( examResult.getExam().getDurationHours() > 0 ) {
                calendar.add( Calendar.HOUR, examResult.getExam().getDurationHours() );
            }
            if ( examResult.getExam().getDurationMinutes() > 0 ) {
                calendar.add( Calendar.MINUTE, examResult.getExam().getDurationMinutes() );
            }
        }
        return calendar.getTime();
    }
}
