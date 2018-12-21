package ru.rsmu.tempoLW.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.TestingPlan;

import java.util.List;

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

    public void onActivate() {
        testingPlans = questionDao.findTestingPlans();
    }
}
