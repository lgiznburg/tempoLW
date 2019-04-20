package ru.rsmu.tempoLW.dao.internal;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.entities.Testee;

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
    public ExamSchedule findExamForTestee( Testee testee ) {
        Criteria criteria = session.createCriteria( ExamSchedule.class )
                .add( Restrictions.eq( "examDate", new Date() ) )
                .createAlias( "testees", "testees"  )
                .add( Restrictions.eq( "testees.id", testee.getId() ) )
                .setMaxResults( 1 );
        return (ExamSchedule) criteria.uniqueResult();
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
}
