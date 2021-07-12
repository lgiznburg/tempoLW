package ru.rsmu.tempoLW.services.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.codehaus.jackson.map.ObjectMapper;
import ru.rsmu.tempoLW.dao.EmailDao;
import ru.rsmu.tempoLW.dao.SystemPropertyDao;
import ru.rsmu.tempoLW.dao.TesteeDao;
import ru.rsmu.tempoLW.dao.UserDao;
import ru.rsmu.tempoLW.entities.EmailQueue;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.ExamToTestee;
import ru.rsmu.tempoLW.entities.system.StoredPropertyName;
import ru.rsmu.tempoLW.services.EmailService;
import ru.rsmu.tempoLW.services.EmailType;
import ru.rsmu.tempoLW.services.TesteeCredentialsService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author leonid.
 */
public class TesteeCredentialsServiceImpl implements TesteeCredentialsService {

    @Inject
    private UserDao userDao;

    @Inject
    private TesteeDao testeeDao;

    @Inject
    private EmailDao emailDao;

    @Inject
    private EmailService emailService;

    @Inject
    private SystemPropertyDao systemPropertyDao;

    @Override
    public List<List<String>> createPasswordsAndEmails( ExamSchedule exam ) {

        List<List<String>> table = new ArrayList<>();
        exam.getExamToTestees().sort( new Comparator<ExamToTestee>() {
            @Override
            public int compare( ExamToTestee o1, ExamToTestee o2 ) {
                return o1.getTestee().getLastName().compareTo( o2.getTestee().getLastName() );
            }
        } );
        for ( ExamToTestee examToTestee : exam.getExamToTestees() ) {
            if ( StringUtils.isBlank( examToTestee.getPassword() ) ) {
                // create new password for each testee
                String password = RandomStringUtils.randomAlphanumeric( 8 )
                        .replace( 'l', 'k' )
                        .replace( 'I', 'N' )
                        .replace( 'O', 'W' )
                        .replace( 'o', 'w' )
                        .replace( '1', '7' ) //exclude symbols which can be miss read
                        .toLowerCase();
                examToTestee.setPassword( userDao.encrypt( password ) );
                examToTestee.setSecretKey( password );
                Calendar expDate = Calendar.getInstance();
                expDate.setTime( exam.getExamDate() );
                expDate.add( Calendar.DAY_OF_YEAR, 1 );
                //examToTestee.setExpirationDate( expDate.getTime() );
                testeeDao.save( examToTestee );
            }

            // password ouput
            //"Номер дела", "ФИО", "ФИО", "Логин", "Пароль"
            List<String> row = new ArrayList<>();
            row.add( examToTestee.getTestee().getCaseNumber() );
            row.add( examToTestee.getTestee().getEmail() != null ? examToTestee.getTestee().getEmail() : examToTestee.getTestee().getFullName() );       // use email
            row.add( examToTestee.getTestee().getFullName() );
            row.add( examToTestee.getTestee().getLogin() );
            row.add( examToTestee.getSecretKey() );
            table.add( row );

            sendCredentialsEmail( examToTestee );

        }
        return table;
    }

    @Override
    public void sendCredentialsEmail( ExamToTestee examToTestee ) {
        ExamSchedule exam = examToTestee.getExam();

        String examDate = new SimpleDateFormat( "dd MMMM yyyy", Locale.forLanguageTag( exam.getTestingPlan().getSubject().getLocale() ) )
                .format( exam.getExamDate() );
        String examDuration = formatDuration( exam );
        String emailTemplate = systemPropertyDao.getProperty( StoredPropertyName.EMAIL_TEMPLATE );
        EmailType emailType = EmailType.findForLocale( emailTemplate, exam.getTestingPlan().getSubject().getLocale() );

        SimpleDateFormat stimef = new SimpleDateFormat("dd/MM HH:mm");
        if ( (exam.isUseProctoring() || exam.isSendEmails()) && StringUtils.isNotBlank( examToTestee.getTestee().getEmail() ) ) {

            if ( exam.isSendEpguOnly() && !examToTestee.getTestee().isGosuslugi() ) {
                return;
            }
            // need to send email to testee
            Map<String,Object> model = new HashMap<>();

            String thisServerUri = systemPropertyDao.getProperty( StoredPropertyName.MY_OWN_URI );
            model.put( "fullName", examToTestee.getTestee().getFullName() );
            model.put( "examName", exam.getTestingPlan().getSubject().getTitle() );
            model.put( "eventType", exam.getTestingPlan().getName() );
            model.put( "examDate", examDate );
            model.put( "useProctoring", exam.isUseProctoring() );
            model.put( "serverAddress", thisServerUri );
            model.put( "examDuration", examDuration );
            model.put( "testeeLogin", examToTestee.getTestee().getLogin() );
            model.put( "testeePassword", examToTestee.getSecretKey() );
            if ( exam.getPeriodStartTime() != null && exam.getPeriodEndTime() != null ) {
                model.put( "examPeriod", String.format( "%s - %s",
                        stimef.format( exam.getPeriodStartTime() ),
                        stimef.format( exam.getPeriodEndTime() )) );
            }

            EmailQueue emailQueue = new EmailQueue();
            emailQueue.setEmailType( emailType );
            emailQueue.setTestee( examToTestee.getTestee() );
            ObjectMapper mapper = new ObjectMapper();
            try {
                emailQueue.setModel( mapper.writeValueAsString( model ) );
            } catch (IOException e) {
                // do nothing
            }
            emailDao.save( emailQueue );

            //emailService.sendEmail( examToTestee.getTestee(), emailType, model );
        }

    }

    private String formatDuration( ExamSchedule exam ) {
        StringBuilder builder = new StringBuilder();
        if ( exam.getTestingPlan().getSubject().getLocale().equals( "ru" ) ) {
            if ( exam.getDurationHours() > 0 ) {
                builder.append( exam.getDurationHours() );
                int hours = exam.getDurationHours();
                if ( hours > 20 ) {
                    hours = hours % 10;
                }
                if ( hours == 1 ) {
                    builder.append( " час" );
                }
                else if ( hours < 5 ) {
                    builder.append( " часа" );
                }
                else {
                    builder.append( " часов" );
                }
            }
            if ( exam.getDurationMinutes() > 0 ) {
                if ( builder.length() > 0 ) builder.append( " " );
                builder.append( exam.getDurationMinutes() )
                        .append( " минут" );
            }
        }
        else if ( exam.getTestingPlan().getSubject().getLocale().equals( "en" ) ){
            if ( exam.getDurationHours() > 0 ) {
                builder.append( exam.getDurationHours() );
                builder.append( " hour" );
                if ( exam.getDurationHours() > 1 ) {
                    builder.append( "s" );
                }
            }
            if ( exam.getDurationMinutes() > 0 ) {
                if ( builder.length() > 0 ) builder.append( " " );
                builder.append( exam.getDurationMinutes() )
                        .append( " minutes" );
            }
        }
        return builder.toString();
    }
}
