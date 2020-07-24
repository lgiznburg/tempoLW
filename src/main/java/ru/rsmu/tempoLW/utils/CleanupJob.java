package ru.rsmu.tempoLW.utils;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author leonid.
 *
 * Clean up result. It testee did not finish his exam this job will finish them.
 * Looking for exams started earlier than exam duration minus 10 minutes
 * or any exams at 10 minutes after exam end period.
 */
public class CleanupJob implements Job {

    @Inject
    private ExamDao examDao;

    @Override
    public void execute( JobExecutionContext context ) throws JobExecutionException {

        List<ExamSchedule>  exams = examDao.findAllExamsToday();

        for ( ExamSchedule examSchedule : exams ) {
            List<ExamResult> rottenResults = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            if ( examSchedule.getPeriodEndTime() == null ) {
                calendar.add( Calendar.MINUTE, examSchedule.getDurationMinutes() * (-1) );
                calendar.add( Calendar.HOUR, examSchedule.getDurationHours() * (-1) );
                calendar.add( Calendar.MINUTE, -10 ); // additional minutes
                rottenResults = examDao.findOldExamResults( examSchedule, calendar.getTime() );
            }
            else {
                calendar.add( Calendar.MINUTE, -10 ); // additional minutes
                if ( examSchedule.getPeriodEndTime().before( calendar.getTime() ) ) {
                    rottenResults = examDao.findOldExamResults( examSchedule, null );
                }
            }

            for ( ExamResult examResult : rottenResults ) {
                examResult.setEndTime( new Date() );
                examDao.save( examResult );
            }
        }

    }
}
