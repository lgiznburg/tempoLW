package ru.rsmu.tempoLW.pages.admin;

import com.rtfparserkit.parser.IRtfParser;
import com.rtfparserkit.parser.IRtfSource;
import com.rtfparserkit.parser.RtfStreamSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import ru.rsmu.rtf.FieldModifier;
import ru.rsmu.rtf.TableModifier;
import ru.rsmu.rtf.model.RtfDocument;
import ru.rsmu.rtf.parser.TemplateRtfListener;
import ru.rsmu.rtf.parser.TemplateRtfParser;
import ru.rsmu.tempoLW.consumabales.AttachmentExcel;
import ru.rsmu.tempoLW.consumabales.AttachmentRtf;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.RtfTemplateDao;
import ru.rsmu.tempoLW.dao.TesteeDao;
import ru.rsmu.tempoLW.consumabales.FileNameTransliterator;
import ru.rsmu.tempoLW.entities.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExamGeneralStatement {

    @PageActivationContext
    private ExamSchedule exam;

    @Inject
    private ExamDao examDao;

    @Inject
    private TesteeDao testeeDao;

    @Inject
    private RtfTemplateDao rtfTemplateDao;

    @Inject
    private Locale currentLocale;

    @Inject
    Logger logger;

    public StreamResponse onActivate() throws IOException {
        DocumentTemplate template = rtfTemplateDao.findByType( DocumentTemplateType.EXAM_STATEMENT );
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
        TableModifier tm = new TableModifier();

        fm.put( "examName", StringUtils.join( exam.getTestingPlan().getSubject().getTitle(), " ( ",
                exam.getTestingPlan().getName(), " )" ) );
        fm.put( "examDate", new SimpleDateFormat( "dd MMMM yyyy", currentLocale ).format( exam.getExamDate() ) );

        List<List<String>> resultsTable = new LinkedList<>();
        List<ExamResult> results = examDao.findExamResults( exam );
        int num = 1;

        if (exam.getExamToTestees().size() != 0) {
            exam.getExamToTestees().sort( new Comparator<ExamToTestee>() {
                @Override
                public int compare( ExamToTestee o1, ExamToTestee o2 ) {
                    return o1.getTestee().getLastName().compareTo( o2.getTestee().getLastName() );
                }
            } );
            // print results
            for (ExamToTestee examToTestee : exam.getExamToTestees() ) {
                List<String> row = new ArrayList<>();
                row.add( String.valueOf( num++ ) );
                row.add( examToTestee.getTestee().getCaseNumber() );
                row.add( examToTestee.getTestee().getLastName() );

                ExamResult result = findResultForTestee( examToTestee.getTestee(), results );
                if ( result == null ) {
                    row.add( "н/я" );
                } else if ( result.isFinished() ){
                    row.add( String.valueOf( result.getMarkTotal() ) );
                } else {
                    row.add( "не завершено" );
                }
                resultsTable.add( row );
            }
        }
        tm.put( "T1", resultsTable );

        fm.modify( doc );
        tm.modify( doc );
        //convert table to bytearray and return
        ByteArrayOutputStream document = new ByteArrayOutputStream();
        doc.output( document );

        String filename = "exam-" + FileNameTransliterator.transliterateRuEn(exam.getName()).replaceAll("\\s", "_") + "-" + new SimpleDateFormat("dd_MM_yyyy").format(exam.getExamDate()) + "-statement.rtf";
        return new AttachmentRtf(document.toByteArray(), filename);
    }

    private ExamResult findResultForTestee( Testee testee, List<ExamResult> results ) {
        for ( ExamResult result : results ) {
            if ( result.getTestee().getCaseNumber().equals( testee.getCaseNumber() ) ) {
                return result;
            }
        }
        return null;
    }
}