package ru.rsmu.tempoLW.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.security.services.SecurityService;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.Testee;
import ru.rsmu.tempoLW.entities.TestingPlan;
import ru.rsmu.tempoLW.services.SecurityUserHelper;

import java.util.List;
import java.util.Locale;

/**
 * @author leonid.
 */
public class Index {

    @Property
    private List<TestingPlan> testingPlans;

    @Property
    private TestingPlan plan;

    @Property
    private boolean examDay;

    @Property
    private Testee testee;

    @Property
    private ExamSchedule assignedExam;

    @Property
    private ExamResult examResult;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private ExamDao examDao;

    @Inject
    private SecurityUserHelper securityUserHelper;

    @Inject
    private SecurityService securityService;

    @Inject
    private Locale currentLocale;


    public void onActivate() {
        testingPlans = questionDao.findTestingPlans( currentLocale.getLanguage() );

        examDay = examDao.findExamToday() != null;
        if ( examDay ) {
            testee = securityUserHelper.getCurrentTestee();
            if ( testee != null ) {
                assignedExam = examDao.findExamForTestee( testee );
                examResult = examDao.findExamResultForTestee( assignedExam, testee );
            }
        }
    }

    public boolean isExamFinished() {
        return examResult != null && examResult.isFinished();
    }

    public void onLogoutTestee()
    {
        if ( securityService.isUser() ) {
            securityService.getSubject().logout();
        }
    }

}
