package ru.rsmu.tempoLW.dao.internal;

import org.hibernate.Criteria;
import org.hibernate.criterion.*;
import org.hibernate.transform.AliasToBeanResultTransformer;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author leonid.
 */
public class QuestionDaoImpl extends BaseDaoImpl implements QuestionDao {
    @Override
    public SubTopic findTopicByName( String topicTitle, ExamSubject subject ) {
        Criteria criteria = session.createCriteria( SubTopic.class )
                .add( Restrictions.eq( "title", topicTitle ) )
                .add( Restrictions.eq( "subject", subject ) )
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
    public List<TestingPlanRule> prepareTestingPlan( ExamSubject subject ) {
        Criteria criteria = session.createCriteria( QuestionInfo.class )
                .add( Restrictions.eq( "subject", subject ) )
                .setProjection( Projections.projectionList()
                        .add( Projections.groupProperty( "topic" ), "topic" )
                        .add( Projections.groupProperty( "complexity" ), "complexity" )
                        .add( Projections.groupProperty( "maxScore" ), "maxScore" )
                        .add( Projections.rowCount(), "totalQuestions" )
                )
                .setResultTransformer( new AliasToBeanResultTransformer( TestingPlanRule.class ) );
        return (List<TestingPlanRule>) criteria.list();
    }

    @Override
    public Question findNextQuestion( long id, ExamSubject subject ) {
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
    public Question findPrevQuestion( long id, ExamSubject subject ) {
        if ( id <= 0 ) return null;
        Criteria criteria = session.createCriteria( Question.class )
                .createAlias( "questionInfo", "questionInfo" )
                .add( Restrictions.eq( "questionInfo.subject", subject ) )
                .addOrder( Order.desc( "id" ) )
                .add( Restrictions.lt( "id", id ) )
                .setMaxResults( 1 );
        return (Question) criteria.uniqueResult();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<TestingPlan> findTestingPlans() {
        Criteria criteria = session.createCriteria( TestingPlan.class )
                .createAlias( "subject", "subject" )
                .add( Restrictions.eq( "enabled", true ) )
                .addOrder( Order.asc( "subject.title" ) );
        return criteria.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<TestingPlan> findTestingPlan( ExamSubject subject ) {
        Criteria criteria = session.createCriteria( TestingPlan.class )
                .add( Restrictions.eq( "subject", subject ) );
        return criteria.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<SubTopic> findTopicsOfSubject( ExamSubject subject ) {
        Criteria criteria = session.createCriteria( SubTopic.class )
                .add( Restrictions.eq( "subject", subject ) );
        return criteria.list();
    }

    @Override
    public long findTopicsCount( ExamSubject subject ) {
        Criteria criteria = session.createCriteria( SubTopic.class )
                .add( Restrictions.eq( "subject", subject ) )
                .setProjection( Projections.rowCount() );
        return (long) criteria.uniqueResult();
    }

    @Override
    public long findQuestionsCount( ExamSubject subject ) {
        Criteria criteria = session.createCriteria( Question.class )
                .createAlias( "questionInfo", "questionInfo" )
                .add( Restrictions.eq( "questionInfo.subject", subject ) )
                .setProjection( Projections.rowCount() );

        return (long) criteria.uniqueResult();
    }


    @Override
    @SuppressWarnings( "unchecked" )
    public List<Question> findRandomQuestions( TestingPlanRule rule ) {

        Criteria criteria = session.createCriteria( Question.class )
                .createAlias( "questionInfo", "questionInfo" )
                .add( Restrictions.eq( "questionInfo.complexity", rule.getComplexity() ) )
                .add( Restrictions.eq( "questionInfo.maxScore", rule.getMaxScore()) )
                .add( Restrictions.in( "questionInfo.topic", rule.getTopics() ) );
        List<Question> questions = criteria.list();
        Collections.shuffle( questions );
        Collections.shuffle( rule.getTopics() );
        List<Question> resultQuestions = new ArrayList<>();
        while ( questions.size() > 0 && resultQuestions.size() < rule.getQuestionCount() ) {
            for ( SubTopic topic : rule.getTopics() ) {
                for ( Question question :questions ) {
                    if ( question.getQuestionInfo().getTopic().equals( topic ) && !resultQuestions.contains( question ) ) {
                        resultQuestions.add( question );
                        break;
                    }
                }
                if ( resultQuestions.size() == rule.getQuestionCount() ) {
                    break;
                }
            }
        }
        return resultQuestions;
    }
}
