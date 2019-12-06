package ru.rsmu.tempoLW.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.security.services.SecurityService;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.services.SecurityUserHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author leonid.
 */
public class Index {

    @Property
    private List<TestingPlan> testingPlans;

    @Property
    private List<TestingPlan> hiddenPlans;

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

    @Property
    @SessionState
    private String examKey;

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

    @Property
    private List<String> output;


    public void onActivate() {
        testingPlans = questionDao.findTestingPlans( currentLocale.getLanguage() );

        if ( testingPlans != null && testingPlans.size() > 0 ) {
            if (hiddenPlans == null) {
                hiddenPlans = new ArrayList<>();
            }
            for (int i = 0; i < testingPlans.size(); i++) {
                TestingPlan plan = testingPlans.get( i );
                if (!plan.isDisplayed()) {
                    hiddenPlans.add(plan);
                    testingPlans.remove( plan );
                    i--;
                }
            }
        }

        examDay = examDao.findExamToday() != null;
        if ( examDay ) {
            testee = securityUserHelper.getCurrentTestee();
            if ( testee != null ) {
                ExamToTestee examToTestee = examDao.findExamForTestee( testee, examKey );
                if ( examToTestee != null ) {
                    assignedExam = examToTestee.getExam();
                    examResult = examDao.findExamResultForTestee( assignedExam, testee );
                }
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
