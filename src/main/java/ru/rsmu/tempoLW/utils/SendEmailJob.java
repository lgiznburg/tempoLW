package ru.rsmu.tempoLW.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import ru.rsmu.tempoLW.dao.EmailDao;
import ru.rsmu.tempoLW.entities.EmailQueue;
import ru.rsmu.tempoLW.entities.EmailQueueStatus;
import ru.rsmu.tempoLW.services.EmailService;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author leonid.
 */
public class SendEmailJob implements Job {

    @Inject
    private EmailDao emailDao;

    @Inject
    private EmailService emailService;

    @Override
    public void execute( JobExecutionContext context ) throws JobExecutionException {
        List<EmailQueue> portion = emailDao.findNewEmails();
        for ( EmailQueue emailQueue : portion ) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<String, Object> parsed = mapper.readValue( emailQueue.getModel(), new TypeReference<Map<String, Object>>() {} );

                emailService.sendEmail( emailQueue.getTestee(), emailQueue.getEmailType(), parsed );

                emailQueue.setStatus( EmailQueueStatus.SUCCESS );
            }
            catch (Exception e) {
                emailQueue.setStatus( EmailQueueStatus.ERROR );
            }
            emailQueue.setUpdatedDate( new Date() );
            emailDao.save( emailQueue );
        }
    }
}
