package ru.rsmu.tempoLW.pages;

import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.Testee;
import ru.rsmu.tempoLW.entities.TestingPlan;
import ru.rsmu.tempoLW.services.SecurityUserHelper;
import ru.rsmu.tempoLW.utils.ExamBuilder;

import java.util.Date;

/**
 * @author leonid.
 */
public class StartExam {
    @Property
    @PageActivationContext
    private ExamSchedule exam;

    @Property
    @SessionState
    private ExamResult examResult;

    @Property
    private Testee testee;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private ExamDao examDao;

    @Inject
    private SecurityUserHelper securityUserHelper;

    public Object onActivate() {
        testee = securityUserHelper.getCurrentTestee();
        if ( exam == null || testee == null ) {
            return Index.class;  // exam nor testee should not be NULL
        }
        if ( examResult == null ) {
            // check stored result in case of exam interruption (this is second enter)
            examResult = examDao.findExamResultForTestee( exam, testee );
        }
        if ( examResult != null && examResult.getExam() != null && examResult.getExam().equals( exam ) ) {
            //if ( !examResult.isFinished() ) {
                return TestFinal.class;  // test has been already created and not finished
            //}
            //else {
            //    return null;  // test has been finished !
            //}
        }
        examResult = new ExamBuilder( questionDao ).buildTestVariant( exam.getTestingPlan() );
        examResult.setStartTime( new Date() );  //set now
        examResult.setExam( exam );

        examResult.setTestee( testee );
        examDao.save( examResult );

        return null;
    }

    public int getQuestionNumber() {
        return examResult.getQuestionResults().size();
    }
}
