package ru.rsmu.tempoLW.pages;

import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.Testee;
import ru.rsmu.tempoLW.entities.TestingPlan;
import ru.rsmu.tempoLW.services.SecurityUserHelper;
import ru.rsmu.tempoLW.utils.ExamBuilder;

import java.util.Date;

/**
 * @author leonid.
 */
public class StartTest {

    @Property
    @PageActivationContext
    private TestingPlan testingPlan;

    @Property
    @SessionState
    private ExamResult examResult;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private ExamDao examDao;

    @Inject
    private SecurityUserHelper securityUserHelper;

    public Object onActivate() {
        if ( examResult != null && !examResult.isFinished() &&
                examResult.getQuestionResults() != null ) {
            return TestFinal.class;  // test has been already created and not finished
        }
        examResult = new ExamBuilder( questionDao ).buildTestVariant( testingPlan );
        examResult.setStartTime( new Date() );  //set now

        Testee testee = securityUserHelper.getCurrentTestee();
        if ( testee != null ) { // check exam
            examResult.setTestee( testee );
            examDao.save( examResult );
        }
        return null;
    }

    public int getQuestionNumber() {
        return examResult.getQuestionResults().size();
    }
}
