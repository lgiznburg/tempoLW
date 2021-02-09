package ru.rsmu.tempoLW.dao.internal;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.entities.auth.User;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author leonid.
 */
@SuppressWarnings( "unchecked" )
public class ExamDaoImpl extends BaseDaoImpl implements ExamDao {
    @Override
    public List<ExamSchedule> findExamsOfSubjects( List<ExamSubject> subjects ) {
        return findExamsOfSubjects( subjects, null );
    }

    @Override
    public List<ExamSchedule> findExamsOfSubjects( List<ExamSubject> subjects, Date date ) {
        Criteria criteria = session.createCriteria( ExamSchedule.class );

        if ( date != null ) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime( date );
            calendar.set( Calendar.MONTH, 0 ); // set january
            calendar.set( Calendar.DAY_OF_MONTH, 1 );
            criteria.add( Restrictions.ge( "examDate", calendar.getTime() ) );
        }

        criteria.createAlias( "testingPlan", "plan" )
                .add( Restrictions.in( "plan.subject", subjects ) )
                .addOrder( Order.asc( "examDate" ) );

        return criteria.list();
    }

    @Override
    public List<ExamSchedule> findExams() {
        Criteria criteria = session.createCriteria( ExamSchedule.class )
                .addOrder( Order.asc( "examDate" ) );
        return criteria.list();
    }

    @Override
    public List<ExamSchedule> findExams( Date date ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( date );
        calendar.set( Calendar.MONTH, 0 ); // set january
        calendar.set( Calendar.DAY_OF_MONTH, 1 );
        Criteria criteria = session.createCriteria( ExamSchedule.class )
                .add( Restrictions.ge( "examDate", calendar.getTime() ) )
                .addOrder( Order.asc( "examDate" ) );
        return criteria.list();
    }

    @Override
    public List<ExamSchedule> findUpcomingExams() {
        Criteria criteria = session.createCriteria( ExamSchedule.class )
                .add( Restrictions.ge( "examDate", new Date() ) )
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
        Date current = new Date();
        Criteria criteria = session.createCriteria( ExamToTestee.class )
                .createAlias( "exam", "exam" )
                .createAlias( "testee", "testee" )
                .add( Restrictions.lt( "exam.periodStartTime", current ) )
                .add( Restrictions.ge( "exam.periodEndTime", current ) )
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
    public List<ExamResult> findResultsForTestee( Testee testee ) {
        Criteria criteria = session.createCriteria( ExamResult.class )
                .add( Restrictions.eq( "testee", testee ) );
        return (List<ExamResult>) criteria.list();
    }

    @Override
    public List<ExamResult> findExamResultsForSubject( ExamSubject subject ) {
        Criteria criteria = session.createCriteria( ExamResult.class )
                .createAlias( "testingPlan", "testingPlan" )
                .add( Restrictions.eq( "testingPlan.subject", subject ) );
        return criteria.list();
    }

    @Override
    public ProctoringReport findProctoringReport( String sessionId ) {
        Criteria criteria = session.createCriteria( ProctoringReport.class )
                .add( Restrictions.eq( "sessionId", sessionId ) )
                .setMaxResults( 1 );
        return ((ProctoringReport) criteria.uniqueResult());
    }

    @Override
    public List<ProctoringReport> findProctoringForExam( ExamSchedule exam ) {
        Criteria criteria = session.createCriteria( ProctoringReport.class )
                .createAlias( "examResult", "examResult" )
                .add( Restrictions.eq( "examResult.exam", exam ) );
        return criteria.list();
    }

    @Override
    public List<ExamSchedule> findAllExamsToday() {
        Calendar passingDay = Calendar.getInstance();
        passingDay.add( Calendar.DAY_OF_YEAR, -1 );
        Criteria criteria = session.createCriteria( ExamSchedule.class )
                .add( Restrictions.lt( "periodStartTime", new Date() ) )
                .add( Restrictions.ge( "periodEndTime", passingDay.getTime() ) );
        return criteria.list();
    }

    @Override
    public List<ExamSchedule> findRunningExams() {
        Date current = new Date();
        Criteria criteria = session.createCriteria( ExamSchedule.class )
                .add( Restrictions.lt( "periodStartTime", current ) )
                .add( Restrictions.ge( "periodEndTime", current ) );
        return criteria.list();
    }

    @Override
    public List<ExamResult> findOldExamResults( ExamSchedule examSchedule, Date time ) {
        Criteria criteria = session.createCriteria( ExamResult.class )
                .add( Restrictions.eq( "exam", examSchedule ) )
                .add( Restrictions.isNull( "endTime" ) );
        if ( time != null ) {
            criteria.add( Restrictions.lt( "startTime", time ) );
        }
        return (List<ExamResult>) criteria.list();
    }

    @Override
    public List<ExamResult> findNotAssignedResults( List<ExamSchedule> exams ) {
        Query query = session.createQuery(
                "from ExamResult as er " +
                        "where id not in (select ass.examResult.id from TeacherAssignment as ass) " +
                        "and er.exam in :exams" )
                .setParameterList( "exams", exams );

        return query.list();
    }

    @Override
    public int countAssignedResults( User teacher ) {
        Criteria criteria = session.createCriteria( TeacherAssignment.class )
                .add( Restrictions.eq( "teacher", teacher ) )
                .add( Restrictions.eq( "finished", false ))
                .setProjection( Projections.rowCount() );
        Long count = (Long) criteria.uniqueResult();
        return count.intValue();
    }

    @Override
    public List<ExamResult> findAssignedResults( User teacher, int startIndex, int size ) {
        Query query = session.createQuery( "select ta.examResult from TeacherAssignment ta " +
                "where ta.teacher = :teacher and ta.finished = false" )
                .setParameter( "teacher", teacher )
                .setFirstResult( startIndex )
                .setMaxResults( size );
        return query.list();
    }

    @Override
    public TeacherAssignment findMyAssignment( ExamResult examResult, User teacher ) {
        Criteria criteria = session.createCriteria( TeacherAssignment.class )
                .add( Restrictions.eq( "teacher", teacher ) )
                .add( Restrictions.eq( "examResult", examResult ) )
                .setMaxResults( 1 );
        return (TeacherAssignment) criteria.uniqueResult();
    }
}
