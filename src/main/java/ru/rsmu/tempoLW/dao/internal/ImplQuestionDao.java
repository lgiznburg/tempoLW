package ru.rsmu.tempoLW.dao.internal;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;

import java.util.List;

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
                .add( Restrictions.eq("subject", subject) )
                .setProjection( Projections.projectionList()
                                .add( Projections.property( "topic" ) )
                                .add( Projections.property( "complexity" ) )
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
                .addOrder( Order.asc("id") );
        if ( id > 0 ) {
            criteria.add( Restrictions.gt( "id", id ) );
        }
        criteria.setMaxResults( 1 );
        return (Question) criteria.uniqueResult();
    }
}
