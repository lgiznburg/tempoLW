package ru.rsmu.tempoLW.pages.admin.testingplan;

import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.consumabales.CrudMode;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.TestingPlan;
import ru.rsmu.tempoLW.entities.TestingPlanRule;
import ru.rsmu.tempoLW.pages.admin.Subjects;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author leonid.
 *
 * Page to edit Testing Plan
 */
public class TestingPlanEdit {

    @Property
    @PageActivationContext
    private TestingPlan testingPlan;

    @Inject
    private QuestionDao questionDao;

    @Property
    private TestingPlanRule rule;

    @Property
    private List<TestingPlanRule> additionalRules;

    @InjectComponent("testingPlanForm")
    private Form testingPlanForm;

    @Property
    private final ValueEncoder<TestingPlanRule> ruleEncoder = new TestingPlanRuleEncoder();

    @InjectPage
    private Subjects subjectsPage;

    public void onPrepareForRender() {
        //check if form exists
        if ( testingPlanForm.isValid() ) {
            prepareAdditionalRules();
        }
    }

    public void onPrepareForSubmit() {
        prepareAdditionalRules();
    }

    private void prepareAdditionalRules() {
        additionalRules = questionDao.prepareTestingPlan( testingPlan.getSubject() );

        for ( Iterator<TestingPlanRule> ruleIterator = additionalRules.iterator(); ruleIterator.hasNext(); ) {
            TestingPlanRule addRule = ruleIterator.next();
            for ( TestingPlanRule existedRule : testingPlan.getRules() ) {
                if ( existedRule.compareTo( addRule ) == 0 ) {
                    ruleIterator.remove();
                }
                else {
                    addRule.setTestingPlan( testingPlan );
                }
            }
        }
    }

    public Object onSuccess() {
        for ( Iterator<TestingPlanRule> ruleIterator = testingPlan.getRules().iterator(); ruleIterator.hasNext(); ) {
            TestingPlanRule removing = ruleIterator.next();
            if ( removing.getQuestionCount() <= 0 ) {
                ruleIterator.remove();
                questionDao.delete( removing );
            }
        }
        for ( TestingPlanRule addRule : additionalRules ) {
            if ( addRule.getQuestionCount() > 0 ) {
                testingPlan.getRules().add( addRule );
            }
        }
        questionDao.save( testingPlan );

        subjectsPage.set( testingPlan.getSubject().getId() );
        return subjectsPage;
        //return TestingPlanList.class;
    }


    // This encoder is used in our loop
    public class TestingPlanRuleEncoder implements ValueEncoder<TestingPlanRule> {

        @Override
        public String toClient( TestingPlanRule rule ) {
            return String.valueOf( rule.getId() );
        }

        @Override
        public TestingPlanRule toValue( String clientValue ) {
            Long ruleId = Long.parseLong( clientValue );
            for ( TestingPlanRule rule : testingPlan.getRules() ) {
                if ( ruleId.equals( rule.getId() ) ) {
                    return rule;
                }
            }
            return new TestingPlanRule();
        }
    }

    public Map getLinkParams() {
        Map<String,Object> params = new HashMap<>();
        params.put( "mode", CrudMode.REVIEW );
        params.put( "subjectId", testingPlan.getSubject().getId() );
        return params;
    }
}
