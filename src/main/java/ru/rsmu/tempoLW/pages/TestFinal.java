package ru.rsmu.tempoLW.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.security.services.SecurityService;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.QuestionResult;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.auth.UserRoleName;
import ru.rsmu.tempoLW.services.SecurityUserHelper;

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
        int finalMark = 0;
        for ( QuestionResult questionResult : examResult.getQuestionResults() ) {
            finalMark += questionResult.getMark();
        }
        examResult.setMarkTotal( finalMark );
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

}
