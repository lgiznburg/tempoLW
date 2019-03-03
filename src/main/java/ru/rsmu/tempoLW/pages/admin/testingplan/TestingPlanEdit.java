package ru.rsmu.tempoLW.pages.admin.testingplan;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.util.AbstractSelectModel;
import ru.rsmu.tempoLW.consumabales.CrudMode;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.SubTopic;
import ru.rsmu.tempoLW.entities.TestingPlan;
import ru.rsmu.tempoLW.entities.TestingPlanRule;
import ru.rsmu.tempoLW.pages.admin.Subjects;

import java.util.*;

/**
 * @author leonid.
 *
 * Page to edit Testing Plan
 */
@Import(module = "testingplan")
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

    private Map<String,Long> topicCounts = new HashMap<>();


    public void set( TestingPlan testingPlan ) {
        this.testingPlan = testingPlan;
    }

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
        // put existed rule topics into topicCount map.
        // so the map will be also used as flag for topics that already has a rule
        for (TestingPlanRule existedRule : testingPlan.getRules() ) {
            for ( SubTopic topic : existedRule.getTopics() ) {
                String key = createCountKey( existedRule, topic );
                topicCounts.put( key, -1l );  // dummy value
            }
        }
        List<TestingPlanRule> rules = questionDao.prepareTestingPlan( testingPlan.getSubject() );
        Map<String, TestingPlanRule> rulesMap = new HashMap<>();
        for ( TestingPlanRule rule : rules ) {
            String countersKey = createCountKey( rule, rule.getTopic() );
            Long quantity = topicCounts.get( createCountKey( rule, rule.getTopic() ) );
            if ( quantity == null || quantity > 0 ) {
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
            }
            topicCounts.put( countersKey, rule.getTotalQuestions() );
        }
        additionalRules = new ArrayList<>( rulesMap.values() );

        //count actual questions quantity for rules
        for ( TestingPlanRule rule : testingPlan.getRules() ) {
            for ( SubTopic topic : rule.getTopics() ) {
                Long quantity = topicCounts.get( createCountKey( rule, topic ) );
                rule.setTotalQuestions( rule.getTotalQuestions() + (quantity != null ? quantity : 0)  );
            }
        }
        Collections.sort( testingPlan.getRules() );

    }

    public Object onSuccess() {
        for ( Iterator<TestingPlanRule> ruleIterator = testingPlan.getRules().iterator(); ruleIterator.hasNext(); ) {
            TestingPlanRule removing = ruleIterator.next();
            if ( removing.getQuestionCount() <= 0 || removing.getTopics().size() == 0 ) {
                ruleIterator.remove();
                questionDao.delete( removing );
                continue;
            }
            checkQuestionCount( removing ); // not removing here :)
        }
        for ( TestingPlanRule addRule : additionalRules ) {
            if ( addRule.getQuestionCount() > 0 && addRule.getTopics().size() > 0 ) {
                checkQuestionCount( addRule );
                testingPlan.getRules().add( addRule );
            }
        }
        questionDao.save( testingPlan );

        return null; // stay here to edit possible additional rules
    }

    private void checkQuestionCount( TestingPlanRule currentRule ) {
        // check question count does not exceed total question quantity
        // this check may be redundant
        long questionQuantity = 0;
        for ( SubTopic topic : currentRule.getTopics() ) {
            Long quantity = topicCounts.get( createCountKey( currentRule, topic ) );
            questionQuantity += quantity != null ? quantity : 0;
        }
        if ( currentRule.getQuestionCount() > questionQuantity ) {
            currentRule.setQuestionCount( (int)questionQuantity );
        }
    }

    private String createCountKey( TestingPlanRule rule, SubTopic topic ) {
        return String.format( "%d-%d-%d", rule.getComplexity(), rule.getMaxScore(), topic.getId() );
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
                    Long quantity = topicCounts.get( createCountKey( rule, topic ) );
                    options.add( new OptionModelImpl( String.format( "%s (%d)", topic.getTitle(), quantity != null? quantity:0 ), topic ) );
                }
                return options;
            }
        };
    }

    public ValueEncoder<SubTopic> getTopicEncoder() {
        return new ValueEncoder<SubTopic>() {
            List<SubTopic> topics = questionDao.findTopicsOfSubject( testingPlan.getSubject() );
            @Override
            public String toClient( SubTopic value ) {
                return String.valueOf( value.getId() );
            }

            @Override
            public SubTopic toValue( String clientValue ) {
                Long id = Long.parseLong( clientValue );
                for ( SubTopic topic : topics ) {
                    if ( id == topic.getId() ) {
                        return topic;
                    }
                }
                return null;
            }
        };
    }
}
