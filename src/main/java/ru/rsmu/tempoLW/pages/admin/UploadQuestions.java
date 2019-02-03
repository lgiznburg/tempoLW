package ru.rsmu.tempoLW.pages.admin;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.upload.services.UploadedFile;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.TestSubject;
import ru.rsmu.tempoLW.utils.ImagesExtractor;
import ru.rsmu.tempoLW.utils.QuestionsLoader;

import javax.inject.Inject;
import java.io.IOException;

/**
 * @author leonid.
 */
@RequiresRoles(value = {"admin","subject_admin"}, logical = Logical.OR )
public class UploadQuestions {
    @Property
    @PageActivationContext
    private TestSubject subject;

    @Property
    private UploadedFile file;

    @Property
    private UploadedFile imagesZipFile;


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
        ImagesExtractor imagesExtractor = null;
        if ( imagesZipFile != null && imagesZipFile.getFileName().matches( ".*\\.zip$" ) ) {
            imagesExtractor = new ImagesExtractor();
            imagesExtractor.extractImages( imagesZipFile.getStream() );
        }

        try {
            if ( file.getFileName().matches( ".*\\.xlsx?" ) ) {
                QuestionsLoader loader = new QuestionsLoader( questionDao, subject );
                if ( imagesExtractor != null ) {

                }
                loader.createWorkbook( file.getStream(), file.getFileName().matches( ".*\\.xls" ) )
                .loadFromFile( file.getStream() );
            }
        } catch (IOException e) {
            //
        }

    }

}
