package ru.rsmu.tempoLW.components;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.security.services.SecurityService;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.QuestionResult;
import ru.rsmu.tempoLW.entities.auth.UserRoleName;
import ru.rsmu.tempoLW.pages.Index;

/**
 * @author leonid.
 */
public class QuestionTable {

    /**
     * Current exam - stored in the session
     */
    @SessionState
    @Property
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

    public void setupRender() {
        int finalMark = 0;
        for ( QuestionResult questionResult : examResult.getQuestionResults() ) {
            finalMark += questionResult.getMark();
        }
        examResult.setMarkTotal( finalMark );

    }

    public Object onGoBack()
    {
        examResult = null;
        if ( securityService.isUser() && securityService.hasRole( UserRoleName.testee.name() ) ) {
            securityService.getSubject().logout();
        }
        return Index.class;
    }


}
