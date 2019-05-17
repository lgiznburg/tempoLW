package ru.rsmu.tempoLW.pages.admin;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import ru.rsmu.tempoLW.consumabales.AttachmentExcel;
import ru.rsmu.tempoLW.consumabales.FileNameTransliterator;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.utils.QuestionExporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author leonid.
 */
public class ExportQuestions {

    @PageActivationContext
    private ExamSubject subject;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private Logger logger;

    public StreamResponse onActivate() {
        QuestionExporter exporter = new QuestionExporter( questionDao, subject );

        String fileName = FileNameTransliterator.transliterateRuEn( subject.getTitle() )
                .replaceAll( "\\W", "_" ) + ".xlsx";

        exporter.doExport( true );

        ByteArrayOutputStream document = new ByteArrayOutputStream();
        try {
            exporter.getWb().write(document);
        } catch (IOException ie) {
            logger.error( "Can't create document", ie );
        }

        return new AttachmentExcel(document.toByteArray(), fileName);
    }

}
