package ru.rsmu.tempoLW.pages.admin;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.upload.services.UploadedFile;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.TestSubject;
import ru.rsmu.tempoLW.utils.QuestionsLoader;

import javax.inject.Inject;
import java.io.IOException;

/**
 * @author leonid.
 */
public class UploadQuestions {
    @Property
    @PageActivationContext
    private TestSubject subject;

    @Property
    private UploadedFile file;


    @Persist(PersistenceConstants.FLASH)
    @Property
    private String message;

    @Inject
    private QuestionDao questionDao;


    Object onUploadException(FileUploadException ex)
    {
        message = "Upload exception: " + ex.getMessage();

        return this;
    }

    public void onSuccess() {
        try {
            if ( file.getFileName().matches( ".*\\.xlsx?" ) ) {
                QuestionsLoader loader = new QuestionsLoader( questionDao, subject );
                loader.createWorkbook( file.getStream(), file.getFileName().matches( ".*\\.xls" ) )
                .loadFromFile( file.getStream() );
            }
        } catch (IOException e) {
            //
        }

    }

}
