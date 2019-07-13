package ru.rsmu.tempoLW.dao.internal;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.entities.*;

import java.util.Date;
import java.util.List;

/**
 * @author leonid.
 */
@SuppressWarnings( "unchecked" )
public class ExamDaoImpl extends BaseDaoImpl implements ExamDao {
    @Override
    public List<ExamSchedule> findExamsOfSubjects( List<ExamSubject> subjects ) {
        Criteria criteria = session.createCriteria( ExamSchedule.class )
                .createAlias( "testingPlan", "plan" )
                .add( Restrictions.in( "plan.subject", subjects ) );

        return criteria.list();
    }

    @Override
    public List<ExamSchedule> findExams() {
        Criteria criteria = session.createCriteria( ExamSchedule.class )
                .addOrder( Order.asc( "examDate" ) );
        return criteria.list();
    }

    @Override
    public ExamSchedule findExamToday() {
        Criteria criteria = session.createCriteria( ExamSchedule.class )
                .add( Restrictions.eq( "examDate", new Date() ) )
                .setMaxResults( 1 );
        return (ExamSchedule) criteria.uniqueResult();
    }

    @Override
    public ExamToTestee findExamForTestee( Testee testee, String password ) {
        Criteria criteria = session.createCriteria( ExamToTestee.class )
                .createAlias( "exam", "exam" )
                .createAlias( "testee", "testee" )
                .add( Restrictions.eq( "exam.examDate", new Date() ) )
                .add( Restrictions.eq( "testee.id", testee.getId() ) )
                .add( Restrictions.eq( "password", password ) )
                .setMaxResults( 1 );
        return (ExamToTestee) criteria.uniqueResult();
    }

    @Override
    public List<ExamResult> findExamResults( ExamSchedule exam ) {
        Criteria criteria = session.createCriteria( ExamResult.class )
                .add( Restrictions.eq( "exam", exam ) );
        return criteria.list();
    }

    @Override
    public ExamResult findExamResultForTestee( ExamSchedule exam, Testee testee ) {
        Criteria criteria = session.createCriteria( ExamResult.class )
                .add( Restrictions.eq( "exam", exam ) )
                .add( Restrictions.eq( "testee", testee ) )
                .setMaxResults( 1 );
        return (ExamResult) criteria.uniqueResult();
    }

    @Override
    public List<ExamResult> findExamResultsForSubject( ExamSubject subject ) {
        Criteria criteria = session.createCriteria( ExamResult.class )
                .createAlias( "testingPlan", "testingPlan" )
                .add( Restrictions.eq( "testingPlan.subject", subject ) );
        return criteria.list();
    }
}
