package ru.rsmu.tempoLW.services.impl;

import org.apache.tapestry5.ioc.LoggerSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.quartz.*;
import ru.rsmu.tempoLW.services.RunJobsService;
import ru.rsmu.tempoLW.utils.CleanupJob;

/**
 * @author leonid.
 */
public class RunJobsServiceImpl implements RunJobsService {

    @Inject
    Scheduler scheduler;

    @Inject
    LoggerSource loggerSource;

    public RunJobsServiceImpl() {

    }

    public void starUp() {
        JobDetail cleanupJob = JobBuilder.newJob( CleanupJob.class ).build();

        ScheduleBuilder<?> scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMinutes( 2 ).repeatForever();
        Trigger cleanupTrigger = TriggerBuilder.newTrigger()
                .withSchedule( scheduleBuilder )
                .forJob( cleanupJob )
                .build();

        try {
            scheduler.scheduleJob( cleanupJob, cleanupTrigger );
        } catch (SchedulerException e) {
            loggerSource.getLogger( RunJobsServiceImpl.class ).error( "Can't start exams cleanup job", e );
        }
    }
}
