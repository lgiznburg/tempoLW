package ru.rsmu.tempoLW.pages.admin.testingplan;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.consumabales.FieldCopy;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.encoders.SubTopicEncoder;
import ru.rsmu.tempoLW.entities.TestSubject;
import ru.rsmu.tempoLW.entities.TestingPlan;
import ru.rsmu.tempoLW.entities.TestingPlanRule;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * @author leonid.
 */
public class TestingPlanCreate {

    @Property
    @PageActivationContext
    private TestSubject subject;

    @Property
    private TestingPlan testingPlan;

    @InjectComponent("testingPlanForm")
    private Form testingPlanForm;

/*
    @InjectComponent("questionCountField")
    private TextField questionCountField;
*/

    @Inject
    private QuestionDao questionDao;

    //
    private int rowNum;
    private Map<Integer, FieldCopy> questionCountFieldMap;

    @Property
    private TestingPlanRule rule;



    public void onPrepareForRender() {
        //check if form exists
        if ( testingPlanForm.isValid() ) {
            prepareTestingPlan( subject );
        }
    }

    public void onPrepareForSubmit() {
        prepareTestingPlan( subject );
    }

    public SubTopicEncoder getSubTopicEncoder() {
        return new SubTopicEncoder( questionDao );
    }

    private void prepareTestingPlan( TestSubject subject ) {
        testingPlan = new TestingPlan();
        testingPlan.setSubject( subject );
        testingPlan.setRules( questionDao.prepareTestingPlan( subject ) );
        for ( TestingPlanRule rule : testingPlan.getRules() ) {
            rule.setTestingPlan( testingPlan );
        }
        Collections.sort( testingPlan.getRules() );
    }

    public Object onSuccess() {
        for ( Iterator<TestingPlanRule> ruleIterator = testingPlan.getRules().iterator(); ruleIterator.hasNext(); ) {
            TestingPlanRule currentRule = ruleIterator.next();
            if ( currentRule.getQuestionCount() <= 0 ) {
                ruleIterator.remove();
                continue;
            }
            if ( currentRule.getQuestionCount() > currentRule.getTotalQuestions() ) {
                currentRule.setQuestionCount( (int)currentRule.getTotalQuestions() );
            }
        }
        questionDao.save( testingPlan );

        return TestingPlanList.class;
    }
}
