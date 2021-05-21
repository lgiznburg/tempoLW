package ru.rsmu.tempoLW.pages.admin;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;
import org.slf4j.Logger;
import ru.rsmu.tempoLW.consumabales.AttachmentPlainText;
import ru.rsmu.tempoLW.consumabales.FileNameTransliterator;
import ru.rsmu.tempoLW.consumabales.appeal.QuestionResultFacade;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.QuestionResult;
import ru.rsmu.tempoLW.entities.Testee;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
public class AppealLatexStatement {

    @PageActivationContext(index = 0)
    private ExamSchedule exam;

    @PageActivationContext(index = 1)
    private Testee testee;

    @Inject
    private ExamDao examDao;

    @Inject
    private Locale currentLocale;

    @Inject
    Logger logger;

    private VelocityEngine velocityEngine;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
    private SimpleDateFormat sdf_file = new SimpleDateFormat("dd_MM_yyyy");

    public StreamResponse onActivate() {
        if ( exam != null && testee != null ) {
            velocityEngine = new VelocityEngine();
            try {
                Properties velocityProp = new Properties();
                InputStream velocityFile = getClass().getResourceAsStream( "/velocity.properties" );
                velocityProp.load( velocityFile );
                velocityEngine.init( velocityProp );
            } catch (Exception e) {
                logger.error( "Can't open velocity.properties file", e );
                return null;
            }

            Map<String,Object> model = new HashMap<>();

            model.put( "exam", exam );
            model.put( "testee", testee );
            model.put( "examDate", sdf.format( exam.getExamDate() ) );

            ExamResult result = examDao.findExamResultForTestee(exam, testee);
            if(result != null) {
                result.getQuestionResults().sort( Comparator.comparingInt( QuestionResult::getOrderNumber ) );
                List<QuestionResultFacade> resultFacades = result.getQuestionResults().stream().map( QuestionResultFacade::new ).collect( Collectors.toList());

                model.put( "results", resultFacades );
                model.put( "finalMark", result.getMarkTotal() );
            }

            try {
                final StringWriter message = new StringWriter();
                final ToolManager toolManager = new ToolManager();
                final ToolContext toolContext = toolManager.createContext();
                final VelocityContext context = new VelocityContext(model, toolContext);

                velocityEngine.mergeTemplate( "appeal/AppealLatex.vm", "UTF-8", context, message );

                String filename = testee.getCaseNumber() + "_"
                        + FileNameTransliterator.transliterateRuEn(exam.getTestingPlan().getSubject().getTitle()).replaceAll("\\s", "_")
                        + "-" + sdf_file.format(exam.getExamDate()) + "-appeal.tex";

                return new AttachmentPlainText( message.toString().getBytes( StandardCharsets.UTF_8 ), filename );

            } catch (Exception e) {
                logger.error( "Velocity: Can't create latex template", e );
                return null;
            }

        }
        return null;
    }
}
