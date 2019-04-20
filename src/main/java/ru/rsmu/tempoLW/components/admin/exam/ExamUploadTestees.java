package ru.rsmu.tempoLW.components.admin.exam;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.TesteeDao;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.Testee;
import ru.rsmu.tempoLW.utils.TesteeLoader;

import java.io.IOException;
import java.util.List;

/**
 * @author leonid.
 */
@RequiresRoles(value = "admin" )
public class ExamUploadTestees {
    @Parameter(required = true)
    @Property
    private Long examId;

    @Property
    private ExamSchedule exam;

    @Property
    private UploadedFile file;

    @Persist(PersistenceConstants.FLASH)
    @Property
    private String message;

    @Inject
    private ExamDao examDao;

    @Inject
    private TesteeDao testeeDao;

    @InjectComponent
    private Form uploadForm;

    @Inject
    private ComponentResources componentResources;

    void onPrepareForRenderFromUploadForm() {
        // If fresh start, make sure there's a User object available.
        if (uploadForm.isValid()) {
            exam = examDao.find( ExamSchedule.class, examId );
        }
    }

    void onPrepareForSubmitFromUploadForm() {
        exam = examDao.find( ExamSchedule.class, examId );
    }

    Object onUploadException( FileUploadException ex)
    {
        message = "Upload exception: " + ex.getMessage();
        return this;
    }

    public boolean onSuccess() {
        try {
            if ( file.getFileName().matches( ".*\\.xlsx?" ) ) {
                TesteeLoader loader = new TesteeLoader( testeeDao );
                List<Testee> testees = loader.loadTestee( file.getStream(), file.getFileName().matches( ".*\\.xls" ) );
                if ( exam.getTestees() == null ) {
                    exam.setTestees( testees );
                }
                else {
                    exam.getTestees().addAll( testees );
                }
                examDao.save( exam );
            }
            // We want to tell our containing page explicitly what upload has done, so we trigger a new event with a
            // parameter. It will bubble up because we don't have a handler method for it.
            componentResources.triggerEvent( "UploadDone", new Object[]{examId}, null );

        } catch (IOException e) {
            //
        }

        return true;

    }

}
