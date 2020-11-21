package ru.rsmu.tempoLW.utils;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.QuestionResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author leonid.
 *
 * Clean up result. If testee did not finish his exam this job will finish it.
 * Looking for exams started earlier than exam duration minus 10 minutes
 * or any exams at 10 minutes after exam end period.
 */
public class CleanupJob implements Job {

    public static int ADDITIONAL_MINUTES = 2;  //additional minutes to complete the test


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
                calendar.add( Calendar.MINUTE, -ADDITIONAL_MINUTES ); // additional minutes
                rottenResults = examDao.findOldExamResults( examSchedule, calendar.getTime() );
            }
            else {
                calendar.add( Calendar.MINUTE, -ADDITIONAL_MINUTES ); // additional minutes
                if ( examSchedule.getPeriodEndTime().before( calendar.getTime() ) ) {
                    rottenResults = examDao.findOldExamResults( examSchedule, null );
                }
            }

            for ( ExamResult examResult : rottenResults ) {
                int finalMark = examResult.getQuestionResults().stream().mapToInt( QuestionResult::getMark ).sum();
                examResult.setMarkTotal( finalMark );
                examResult.setEndTime( new Date() );
                examDao.save( examResult );
            }
        }

    }
}
