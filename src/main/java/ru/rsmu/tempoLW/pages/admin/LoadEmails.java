package ru.rsmu.tempoLW.pages.admin;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;
import ru.rsmu.tempoLW.dao.TesteeDao;
import ru.rsmu.tempoLW.utils.TesteeLoader;

import java.io.IOException;

/**
 * @author leonid.
 */
public class LoadEmails {
    @Property
    private UploadedFile file;

    @Persist(PersistenceConstants.FLASH)
    @Property
    private String message;

    @Inject
    private TesteeDao testeeDao;

    @InjectComponent
    private Form uploadForm;


    Object onUploadException( FileUploadException ex)
    {
        message = "Upload exception: " + ex.getMessage();
        return this;
    }

    public boolean onSuccess() {
        try {
            if ( file.getFileName().matches( ".*\\.xlsx?" ) ) {
                TesteeLoader loader = new TesteeLoader( testeeDao );
                loader.loadTesteeEmails( file.getStream(), file.getFileName().matches( ".*\\.xls" ) );
            }

        } catch (IOException e) {
            //
        }

        return true;

    }

}
