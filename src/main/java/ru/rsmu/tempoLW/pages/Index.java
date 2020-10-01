package ru.rsmu.tempoLW.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.security.services.SecurityService;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.services.SecurityUserHelper;

import java.util.*;

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

    @Property
    private List<String> testsLevels;

    @Property
    private String currentLevel;

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

    @Inject
    private Messages messages;


    public void onActivate() {
        testingPlans = questionDao.findTestingPlans( currentLocale.getLanguage() );
        testsLevels = new ArrayList<>();

        if ( testingPlans != null && testingPlans.size() > 0 ) {
            if (hiddenPlans == null) {
                hiddenPlans = new ArrayList<>();
            }
            for (int i = 0; i < testingPlans.size(); i++) {
                TestingPlan plan = testingPlans.get( i );
                if (!plan.isDisplayed()) {  // hide some plans
                    hiddenPlans.add(plan);
                    testingPlans.remove( plan );
                    i--;
                }
                else {
                    if ( !testsLevels.contains( plan.getSubject().getType().name() ) ) {
                        testsLevels.add( plan.getSubject().getType().name() );
                    }
                }
            }
        }
        Collections.sort( testsLevels, new Comparator<String>() {
            @Override
            public int compare( String o1, String o2 ) {
                SubjectType one = SubjectType.valueOf( o1 );
                SubjectType two = SubjectType.valueOf( o2 );
                return one.ordinal() - two.ordinal();
            }
        } );

        examDay = examDao.findExamToday() != null;
        if ( examDay ) {
            testee = securityUserHelper.getCurrentTestee();
            if ( testee != null ) {
                ExamToTestee examToTestee = examDao.findExamForTestee( testee, examKey );
                if ( examToTestee != null ) {
                    assignedExam = examToTestee.getExam();
                    examResult = examDao.findExamResultForTestee( assignedExam, testee );
                    if ( examResult != null ) {
                        Collections.sort( examResult.getQuestionResults() );
                    }
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

    public List<TestingPlan> getCurrentLevelPlans() {
        List<TestingPlan> plans = new ArrayList<>();
        for ( TestingPlan plan : testingPlans ) {
            if ( plan.getSubject().getType().name().equals( currentLevel ) ) {
                plans.add( plan );
            }
        }
        return plans;
    }

    public boolean getCurrentLevelHasTitle() {
        return SubjectType.valueOf( currentLevel ) != SubjectType.UNDEFINED;
    }

    public String getCurrentLevelTitle() {
        return messages.get( currentLevel.toLowerCase() + "-title" );
    }

}
