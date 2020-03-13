package ru.rsmu.tempoLW.pages.admin.testingplan;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ValueEncoderSource;
import org.apache.tapestry5.util.AbstractSelectModel;
import ru.rsmu.tempoLW.consumabales.CrudMode;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.entities.SubTopic;
import ru.rsmu.tempoLW.entities.TestingPlan;
import ru.rsmu.tempoLW.entities.TestingPlanRule;

import java.util.*;

/**
 * @author leonid.
 */
@Import(module = "testingplan")
public class TestingPlanCreate {

    @Property
    @PageActivationContext
    private ExamSubject subject;

    @Property
    private TestingPlan testingPlan;

    @InjectComponent("testingPlanForm")
    private Form testingPlanForm;

    @Inject
    private QuestionDao questionDao;

    @Property
    private TestingPlanRule rule;

    @InjectPage
    private TestingPlanEdit testingPlanEditPage;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    private Map<String,Long> topicCounts = new HashMap<>();

    public void onPrepareForRender() {
        //check if form exists
        if ( testingPlanForm.isValid() ) {
            prepareTestingPlan( subject );
        }
    }

    public void onPrepareForSubmit() {
        prepareTestingPlan( subject );
    }

    private void prepareTestingPlan( ExamSubject subject ) {
        testingPlan = new TestingPlan();
        testingPlan.setSubject( subject );
        List<TestingPlanRule> rules = questionDao.prepareTestingPlan( subject );
        Map<String, TestingPlanRule> rulesMap = new HashMap<>();
        for ( TestingPlanRule rule : rules ) {
            String key = String.format( "%d:%d", rule.getComplexity(), rule.getMaxScore() );
            TestingPlanRule actualRule = rulesMap.get( key );
            if ( actualRule == null ) {
                rulesMap.put( key, rule );
                actualRule = rule;
                actualRule.setTestingPlan( testingPlan );
            }
            if ( actualRule.getTopics() == null ) {
                actualRule.setTopics( new ArrayList<>() );
            }
            actualRule.getTopics().add( rule.getTopic() );
            actualRule.setTotalQuestions( actualRule.getTotalQuestions() + (actualRule != rule ? rule.getTotalQuestions() : 0) );
            topicCounts.put( String.format( "%d-%d-%d", rule.getComplexity(), rule.getMaxScore(), rule.getTopic().getId() ), rule.getTotalQuestions() );
        }

        testingPlan.setRules( new ArrayList<>( rulesMap.values() ) );
        Collections.sort( testingPlan.getRules() );
    }

    public Object onSuccess() {
        for ( Iterator<TestingPlanRule> ruleIterator = testingPlan.getRules().iterator(); ruleIterator.hasNext(); ) {
            TestingPlanRule currentRule = ruleIterator.next();
            if ( currentRule.getQuestionCount() <= 0 ) {
                ruleIterator.remove();
                continue;
            }
            if ( currentRule.getTopics().size() == 0 ) {
                ruleIterator.remove();
                continue;
            }
            // check question count does not exceed total question quantity
            // this check may be redundant
            long questionQuantity = 0;
            for ( SubTopic topic : currentRule.getTopics() ) {
                Long quantity = topicCounts.get( String.format( "%d-%d-%d", currentRule.getComplexity(), currentRule.getMaxScore(), topic.getId() ) );
                questionQuantity += quantity != null ? quantity : 0;
            }
            if ( currentRule.getQuestionCount() > questionQuantity ) {
                currentRule.setQuestionCount( (int)questionQuantity );
            }
        }
        questionDao.save( testingPlan );

        testingPlanEditPage.set( testingPlan );
        return testingPlanEditPage;
    }

    public Map getLinkParams() {
        Map<String,Object> params = new HashMap<>();
        params.put( "mode", CrudMode.REVIEW );
        params.put( "subjectId", subject.getId() );
        return params;
    }

    public SelectModel getCurrentSelectModel() {
        return new AbstractSelectModel() {
            @Override
            public List<OptionGroupModel> getOptionGroups() {
                return null;
            }

            @Override
            public List<OptionModel> getOptions() {
                List<OptionModel> options = new ArrayList<OptionModel>();
                for ( SubTopic topic : rule.getTopics() ) {
                    Long quantity = topicCounts.get( String.format( "%d-%d-%d", rule.getComplexity(), rule.getMaxScore(), topic.getId() ) );
                    options.add( new OptionModelImpl( String.format( "%s (%d)", topic.getTitle(), quantity != null? quantity:0 ), topic ) );
                }
                return options;
            }
        };
    }

    public ValueEncoder<SubTopic> getTopicEncoder() {
        return valueEncoderSource.getValueEncoder( SubTopic.class );
    }
}
