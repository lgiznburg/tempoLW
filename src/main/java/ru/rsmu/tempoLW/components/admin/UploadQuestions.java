package ru.rsmu.tempoLW.components.admin;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.utils.ImagesExtractor;
import ru.rsmu.tempoLW.utils.QuestionsLoader;

import java.io.IOException;

/**
 * @author leonid.
 */
@RequiresRoles(value = {"admin","subject_admin"}, logical = Logical.OR )
public class UploadQuestions {
    @Property
    @Parameter(required = true)
    private Long subjectId;

    @Property
    private ExamSubject subject;

    @Property
    private UploadedFile file;

    @Property
    private UploadedFile imagesZipFile;


    @Persist(PersistenceConstants.FLASH)
    @Property
    private String message;

    @Inject
    private QuestionDao questionDao;

    @InjectComponent
    private Form uploadForm;

    @Inject
    private ComponentResources componentResources;


    void onPrepareForRenderFromUploadForm() {
        // If fresh start, make sure there's a User object available.
        if (uploadForm.isValid()) {
            subject = questionDao.find( ExamSubject.class, subjectId );
        }
    }

    void onPrepareForSubmitFromUploadForm() {
        subject = questionDao.find( ExamSubject.class, subjectId );
    }

    Object onUploadException(FileUploadException ex)
    {
        message = "Upload exception: " + ex.getMessage();

        return this;
    }

    public boolean onSuccess() {
        ImagesExtractor imagesExtractor = null;
        if ( imagesZipFile != null && imagesZipFile.getFileName().matches( ".*\\.zip$" ) ) {
            imagesExtractor = new ImagesExtractor();
            imagesExtractor.extractImages( imagesZipFile.getStream() );
        }

        try {
            if ( file.getFileName().matches( ".*\\.xlsx?" ) ) {
                QuestionsLoader loader = new QuestionsLoader( questionDao, subject );
                if ( imagesExtractor != null ) {
                    loader.setImagesExtractor( imagesExtractor );
                }
                loader.createWorkbook( file.getStream(), file.getFileName().matches( ".*\\.xls" ) )
                .loadFromFile();
            }
            // We want to tell our containing page explicitly what upload has done, so we trigger a new event with a
            // parameter. It will bubble up because we don't have a handler method for it.
            componentResources.triggerEvent( "UploadDone", new Object[]{subjectId}, null );

        } catch (IOException e) {
            //
        }

        return true;
    }

}
