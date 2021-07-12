package ru.rsmu.tempoLW.pages;

import org.apache.commons.lang3.StringUtils;
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
import java.util.stream.Collectors;

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

    @Property
    private String eventGreeting = "";

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
            // select plans which should not be displayed
            hiddenPlans = testingPlans.stream()
                    .filter( pl -> !pl.isDisplayed() )
                    .collect( Collectors.toList());
            // select plans to display
            testingPlans = testingPlans.stream()
                    .filter( TestingPlan::isDisplayed )
                    .collect( Collectors.toList());
            // collect unique names of plan type
            testsLevels.addAll( testingPlans.stream()
                    .map( pl -> pl.getSubject().getType().name() )
                    .collect( Collectors.toSet() )
            );
        }
        testsLevels.sort( new Comparator<String>() {
            @Override
            public int compare( String o1, String o2 ) {
                SubjectType one = SubjectType.valueOf( o1 );
                SubjectType two = SubjectType.valueOf( o2 );
                return one.ordinal() - two.ordinal();
            }
        } );

        List<ExamSchedule> todayExams = examDao.findRunningExams();
        examDay = todayExams != null && todayExams.size() > 0 ;
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

            // create page greeting
            Set<String> eventTypes = todayExams.stream().map( ex -> ex.getTestingPlan().getName() ).collect( Collectors.toSet());
            eventGreeting = messages.format( "exam-day", StringUtils.join( eventTypes, ", " ) );
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
        return testingPlans.stream()
                .filter( pl -> pl.getSubject().getType().name().equals( currentLevel ) )
                .collect( Collectors.toList() );
    }

    public boolean getCurrentLevelHasTitle() {
        return SubjectType.valueOf( currentLevel ) != SubjectType.UNDEFINED;
    }

    public String getCurrentLevelTitle() {
        return messages.get( currentLevel.toLowerCase() + "-title" );
    }

    public boolean getTestsPresent() { return testingPlans != null && testingPlans.size() > 0; }
}
