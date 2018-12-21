package ru.rsmu.tempoLW.dao.internal;

import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.hibernate.transform.AliasToBeanResultTransformer;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author leonid.
 */
public class ImplQuestionDao extends ImplBaseDao implements QuestionDao {
    @Override
    public SubTopic findTopicByName( String topicTitle ) {
        Criteria criteria = session.createCriteria( SubTopic.class )
                .add( Restrictions.eq( "title", topicTitle ) )
                .setMaxResults( 1 );
        return (SubTopic) criteria.uniqueResult();
    }

    @Override
    public QuestionInfo findQuestionInfoByName( String name, SubTopic topic, int complexity ) {
        Criteria criteria = session.createCriteria( QuestionInfo.class )
                .add( Restrictions.eq( "name", name ) )
                .add( Restrictions.eq( "topic", topic ) )
                .add( Restrictions.eq( "complexity", complexity ) )
                .setMaxResults( 1 );

        return (QuestionInfo) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public List<TestingPlanRule> prepareTestingPlan( TestSubject subject ) {
        Criteria criteria = session.createCriteria( QuestionInfo.class )
                .add( Restrictions.eq( "subject", subject ) )
                .setProjection( Projections.projectionList()
                                .add( Projections.groupProperty( "topic" ), "topic" )
                                .add( Projections.groupProperty( "complexity" ), "complexity" )
                                .add( Projections.rowCount(), "totalQuestions" )
                )
                .setResultTransformer( new AliasToBeanResultTransformer( TestingPlanRule.class ) );
        return (List<TestingPlanRule>) criteria.list();
    }

    @Override
    public Question findNextQuestion( long id, TestSubject subject ) {
        Criteria criteria = session.createCriteria( Question.class )
                .createAlias( "questionInfo", "questionInfo" )
                .add( Restrictions.eq( "questionInfo.subject", subject ) )
                .addOrder( Order.asc( "id" ) );
        if ( id > 0 ) {
            criteria.add( Restrictions.gt( "id", id ) );
        }
        criteria.setMaxResults( 1 );
        return (Question) criteria.uniqueResult();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<TestingPlan> findTestingPlans() {
        Criteria criteria = session.createCriteria( TestingPlan.class )
                .add( Restrictions.eq( "enabled", true ) );
        return criteria.list();
    }

    @Override
    public Question findRandomQuestion( TestingPlanRule rule ) {
        Question question = null;
        Criterion restrictions = Restrictions.conjunction()
                .add( Restrictions.eq( "questionInfo.complexity", rule.getComplexity() ) )
                .add( Restrictions.eq( "questionInfo.topic", rule.getTopic() ) );

        Criteria criteria = session.createCriteria( Question.class )
                .createAlias( "questionInfo", "questionInfo" )
                .add( restrictions )
                .setProjection( Projections.rowCount() );
        int count = ((Number) criteria.uniqueResult()).intValue();
        if (0 != count) {
            int index = new Random().nextInt(count);
            criteria = session.createCriteria( Question.class )
                    .createAlias( "questionInfo", "questionInfo" )
                    .add( restrictions )
                    .setFirstResult( index ).setMaxResults( 1 );
            question = (Question) criteria.uniqueResult();
        }
        return question;
    }

    @Override
    public List<Question> findRandomQuestions( TestingPlanRule rule ) {

        Criteria criteria = session.createCriteria( Question.class )
                .createAlias( "questionInfo", "questionInfo" )
                .add( Restrictions.eq( "questionInfo.complexity", rule.getComplexity() ) )
                .add( Restrictions.eq( "questionInfo.topic", rule.getTopic() ) );
        List<Question> questions = criteria.list();
        Collections.shuffle( questions );
        return questions.subList( 0, rule.getQuestionCount() );
    }
}
