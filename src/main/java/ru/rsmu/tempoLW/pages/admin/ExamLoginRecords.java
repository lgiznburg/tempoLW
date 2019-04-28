package ru.rsmu.tempoLW.pages.admin;

import com.tutego.jrtf.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.consumabales.AttachmentRtf;
import ru.rsmu.tempoLW.dao.TesteeDao;
import ru.rsmu.tempoLW.dao.UserDao;
import ru.rsmu.tempoLW.encoders.FileNameTransliterator;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.Testee;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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

    public StreamResponse onActivate() {
        // prepare all paragraphs before creating RTF section
        List<RtfPara> docContent = new LinkedList<>();
        docContent.add(
                RtfPara.p( RtfText.bold(
                RtfText.textJoinWithSpace(  true,
                        exam.getTestingPlan().getSubject().getTitle(), "(",
                        exam.getTestingPlan().getName(), ")" ) )
                ).alignCentered() );
        docContent.add( RtfPara.p( new SimpleDateFormat( "dd MMMM yyyy", currentLocale ).format( exam.getExamDate() ) ) );

        docContent.add( RtfPara.row( "Номер дела", "ФИО", "Логин", "Пароль" )
                .bottomCellBorder()
                .leftCellBorder()
                .topCellBorder()
                .rightCellBorder()
                .cellSpace( 0.4, RtfUnit.CM )
        );
        // check for already assigned passwords ??

        exam.getTestees().sort( new Comparator<Testee>() {
            @Override
            public int compare( Testee o1, Testee o2 ) {
                return o1.getLastName().compareTo( o2.getLastName() );
            }
        } );
        for ( Testee testee : exam.getTestees() ) {
            String password = RandomStringUtils.randomAlphanumeric( 8 );
            testee.setPassword( userDao.encrypt( password ) );
            Calendar expDate = Calendar.getInstance();
            expDate.setTime( exam.getExamDate() );
            expDate.add( Calendar.DAY_OF_YEAR, 1 );
            testee.setExpirationDate( expDate.getTime() );
            docContent.add( RtfPara.row( testee.getCaseNumber(), testee.getLastName(), testee.getLogin(), password )
                    .bottomCellBorder()
                    .leftCellBorder()
                    .topCellBorder()
                    .rightCellBorder()
                    .cellSpace( 0.4, RtfUnit.CM ) );
            testeeDao.save( testee );
        }

        ByteArrayOutputStream document = new ByteArrayOutputStream();

        // create RTF file
        Rtf.rtf()
                .header( RtfHeader.font( RtfHeaderFont.TIMES_ROMAN ).charset( RtfHeaderFont.CharSet.CYRILLIC ).at( 0 ) )
                .section( docContent )
        .out( new OutputStreamWriter( document ) );

        //additional parts of login file name
        FileNameTransliterator trans = new FileNameTransliterator();
        String examName = exam.getName();
        examName = examName.replaceAll("\\s", "_");
        examName = trans.transliterateRuEn(examName);
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
        String examDate = sdf.format(exam.getExamDate());

        return new AttachmentRtf(  document.toByteArray(), "exam_" + examName + "_" + examDate + "_" + "logins.rtf" );
    }
}
