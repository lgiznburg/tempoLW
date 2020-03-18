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

        List<List<String>> table = new ArrayList<>();
        exam.getExamToTestees().sort( new Comparator<ExamToTestee>() {
            @Override
            public int compare( ExamToTestee o1, ExamToTestee o2 ) {
                return o1.getTestee().getLastName().compareTo( o2.getTestee().getLastName() );
            }
        } );
        for ( ExamToTestee examToTestee : exam.getExamToTestees() ) {
            // create new password for each testee if that testee doesn't have one already - only for PKLW test modification
            Testee testee = examToTestee.getTestee();
            String password;
            if ( testee.getPassword() != null && !testee.getPassword().equals("") ) {
                password = testee.getPassword();
                examToTestee.setPassword(userDao.encrypt(password));
            } else {
                password = RandomStringUtils.randomAlphanumeric(8)
                        .replace('l', 'k')
                        .replace('I', 'N')
                        .replace('1', '7') //exclude symbols which can be miss read
                        .toLowerCase();
                examToTestee.setPassword(userDao.encrypt(password));

                testee.setPassword( password );
                testeeDao.save( testee );
            }

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
        String examDate = sdf.format(exam.getExamDate());

        return new AttachmentRtf(  document.toByteArray(), "exam_" + examName + "_" + examDate + "_" + "logins.rtf" );
    }
}
