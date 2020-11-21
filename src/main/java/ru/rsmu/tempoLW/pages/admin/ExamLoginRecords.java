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
import ru.rsmu.tempoLW.services.TesteeCredentialsService;

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
    private Locale currentLocale;

    @Inject
    private RtfTemplateDao rtfTemplateDao;

    @Inject
    private TesteeCredentialsService testeeCredentialsService;

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

        List<List<String>> table = testeeCredentialsService.createPasswordsAndEmails( exam );

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

}
