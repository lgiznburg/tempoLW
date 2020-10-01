package ru.rsmu.tempoLW.pages.admin;

import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfStreamSource;
import com.tutego.jrtf.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.rtf.FieldModifier;
import ru.rsmu.rtf.TableModifier;
import ru.rsmu.rtf.model.RtfDocument;
import ru.rsmu.rtf.parser.TemplateRtfListener;
import ru.rsmu.rtf.parser.TemplateRtfParser;
import ru.rsmu.tempoLW.consumabales.AttachmentRtf;
import ru.rsmu.tempoLW.dao.RtfTemplateDao;
import ru.rsmu.tempoLW.dao.TesteeDao;
import ru.rsmu.tempoLW.dao.UserDao;
import ru.rsmu.tempoLW.consumabales.FileNameTransliterator;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.services.EmailService;
import ru.rsmu.tempoLW.services.EmailType;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author leonid.
 */
public class ExamLoginRecords {
    @Property
    @PageActivationContext
    private ExamSchedule exam;

    @Inject
    private UserDao userDao;

    @Inject
    private TesteeDao testeeDao;

    @Inject
    private Locale currentLocale;

    @Inject
    private RtfTemplateDao rtfTemplateDao;

    @Inject
    private EmailService emailService;

    public StreamResponse onActivate() throws IOException {

        DocumentTemplate template = rtfTemplateDao.findByType( DocumentTemplateType.LOGINS );
        if ( template == null ) {
            throw new RuntimeException("Rtf Template does not exist");
        }

        InputStream is = new ByteArrayInputStream( template.getRtfTemplate().getBytes() );
        IRtfSource source = new RtfStreamSource(is);
        IRtfParser parser = new TemplateRtfParser();
        TemplateRtfListener listener = new TemplateRtfListener();
        parser.parse(source, listener);

        RtfDocument doc = listener.getDocument();

        FieldModifier fm = new FieldModifier();

        fm.put( "examName", StringUtils.join( exam.getTestingPlan().getSubject().getTitle(), " ( ",
                exam.getTestingPlan().getName(), " )" ) );
        fm.put( "examDate", new SimpleDateFormat( "dd MMMM yyyy", currentLocale ).format( exam.getExamDate() ) );
        String examDate = new SimpleDateFormat( "dd MMMM yyyy", Locale.forLanguageTag( exam.getTestingPlan().getSubject().getLocale() ) )
                .format( exam.getExamDate() );
        String examDuration = formatDuration( exam );
        EmailType emailType = EmailType.findForLocale( EmailType.EXAM_PASSWORD_SHORT_NAME, exam.getTestingPlan().getSubject().getLocale() );

        List<List<String>> table = new ArrayList<>();
        exam.getExamToTestees().sort( new Comparator<ExamToTestee>() {
            @Override
            public int compare( ExamToTestee o1, ExamToTestee o2 ) {
                return o1.getTestee().getLastName().compareTo( o2.getTestee().getLastName() );
            }
        } );
        for ( ExamToTestee examToTestee : exam.getExamToTestees() ) {
            // create new password for each testee
            String password = RandomStringUtils.randomAlphanumeric( 8 )
                    .replace( 'l', 'k' )
                    .replace( 'I', 'N' )
                    .replace( '1', '7' ) //exclude symbols which can be miss read
                .toLowerCase();
            examToTestee.setPassword( userDao.encrypt( password ) );
            Calendar expDate = Calendar.getInstance();
            expDate.setTime( exam.getExamDate() );
            expDate.add( Calendar.DAY_OF_YEAR, 1 );
            //examToTestee.setExpirationDate( expDate.getTime() );

            // password ouput
            //"Номер дела", "ФИО", "ФИО", "Логин", "Пароль"
            List<String> row = new ArrayList<>();
            row.add( examToTestee.getTestee().getCaseNumber() );
            row.add( examToTestee.getTestee().getLastName() );
            row.add( examToTestee.getTestee().getLastName() );
            row.add( examToTestee.getTestee().getLogin() );
            row.add( password );
            table.add( row );
            testeeDao.save( examToTestee );

            SimpleDateFormat stimef = new SimpleDateFormat("HH:mm");
            if ( exam.isUseProctoring() && StringUtils.isNotBlank( examToTestee.getTestee().getEmail() ) ) {
                // need to send email to testee
                Map<String,Object> model = new HashMap<>();

                model.put( "fullName", examToTestee.getTestee().getFullName() );
                model.put( "examName", String.format( "%s (%s)", exam.getTestingPlan().getSubject().getTitle(), exam.getTestingPlan().getName() ) );
                model.put( "examDate", examDate );
                model.put( "serverAddress", "https://tempolw.rsmu.ru" );
                model.put( "examDuration", examDuration );
                model.put( "testeeLogin", examToTestee.getTestee().getLogin() );
                model.put( "testeePassword", password );
                if ( exam.getPeriodStartTime() != null && exam.getPeriodEndTime() != null ) {
                    model.put( "examPeriod", String.format( "%s - %s",
                            stimef.format( exam.getPeriodStartTime() ),
                            stimef.format( exam.getPeriodEndTime() )) );
                }

                emailService.sendEmail( examToTestee.getTestee(), emailType, model );
            }
        }

        TableModifier tm = new TableModifier();
        tm.put( "T1", table );

        fm.modify( doc );
        tm.modify( doc );

        ByteArrayOutputStream document = new ByteArrayOutputStream();
        doc.output( document );

        //additional parts of login file name
        String examName = exam.getName();
        examName = examName.replaceAll("\\s", "_");
        examName = FileNameTransliterator.transliterateRuEn(examName);
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
        String dateForFilename = sdf.format(exam.getExamDate());

        return new AttachmentRtf(  document.toByteArray(), "exam_" + examName + "_" + dateForFilename + "_" + "logins.rtf" );
    }

    private String formatDuration( ExamSchedule exam ) {
        StringBuilder builder = new StringBuilder();
        if ( exam.getTestingPlan().getSubject().getLocale().equals( "ru" ) ) {
            if ( exam.getDurationHours() > 0 ) {
                builder.append( exam.getDurationHours() );
                if ( exam.getDurationHours() == 1 ) {
                    builder.append( " час" );
                }
                else if ( exam.getDurationHours() < 5 ) {
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
