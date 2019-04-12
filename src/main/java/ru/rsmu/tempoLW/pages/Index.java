package ru.rsmu.tempoLW.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.QuestionDao;
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

    @Inject
    private QuestionDao questionDao;

    @Inject
    private ExamDao examDao;

    @Inject
    private SecurityUserHelper securityUserHelper;

    @Inject
    private Locale currentLocale;


    public void onActivate() {
        testingPlans = questionDao.findTestingPlans( currentLocale.getLanguage() );
    }

    public boolean isExamDay() {
        ExamSchedule exam = examDao.findExamToday();
        return exam != null;
    }

    public ExamSchedule getAssignedExam() {
        Testee testee = securityUserHelper.getCurrentTestee();
        if ( testee != null ) {
            return examDao.findExamForTestee( testee );
        }
        return null;
    }
}
