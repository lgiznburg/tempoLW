package ru.rsmu.tempoLW.pages.admin;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;
import ru.rsmu.tempoLW.consumabales.AttachmentPlainText;
import ru.rsmu.tempoLW.consumabales.CrudMode;
import ru.rsmu.tempoLW.consumabales.FileNameTransliterator;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.TesteeDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.Testee;
import ru.rsmu.tempoLW.utils.TesteeLoader;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author leonid.
 */
public class ExamResultExport {
    @PageActivationContext
    @Property
    private ExamSchedule exam;

    @Property
    private UploadedFile file;

    @Property
    private Integer tabNumber;

    @Inject
    private ExamDao examDao;

    @Inject
    private TesteeDao testeeDao;

    public void onActivate() {

    }

    public StreamResponse onSuccess() {
        try {
            if ( file.getFileName().matches( ".*\\.xlsx?" ) ) {
                String filename = "exam-" + FileNameTransliterator.transliterateRuEn(exam.getName()).replaceAll("\\s", "_") + "-" + new SimpleDateFormat("dd_MM_yyyy").format(exam.getExamDate()) + "-scores.txt";

                TesteeLoader loader = new TesteeLoader( testeeDao );
                List<Testee> testees = loader.findTestee( file.getStream(), file.getFileName().matches( ".*\\.xls" ) );
                //List<ExamResult> results = examDao.findExamResults( exam );
                List<ExamResult> results = examDao.findExamResultsForSubject( exam.getTestingPlan().getSubject() );

                List<String> script = new LinkedList<>();
                script.add( "WAIT(3)" );

                for ( Testee testee : testees ) {
                    ExamResult result = findResultForTestee( results, testee );
                    if ( result != null && result.isFinished() ) {
                        script.add( String.format( "KEYSTRING(\"%d\")", result.getMarkTotal() ) );
                    }
                    for ( int i = 0; i < tabNumber; i++ ) {
                        script.add( "KEYPRESS(#TAB)" );
                    }

                }
                script.add( "HALT" );

                String finalScript = StringUtils.join( script, "\n" );
                return new AttachmentPlainText( finalScript.getBytes(), filename );

            }
        } catch (IOException e) {
            //
        }
        return null;
    }

    private ExamResult findResultForTestee( List<ExamResult> results, Testee testee ) {
        for ( ExamResult result : results ) {
            if ( result.getTestee().getCaseNumber().equals( testee.getCaseNumber() ) ) {
                return result;
            }
        }
        return null;
    }

    public Map<String, Object> getQueryParams() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put( "examId", exam.getId() );
        queryParams.put( "mode", CrudMode.REVIEW );
        return queryParams;
    }}
