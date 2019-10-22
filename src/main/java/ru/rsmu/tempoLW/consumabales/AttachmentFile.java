
package ru.rsmu.tempoLW.consumabales;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.services.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author leonid.
 */
public abstract class AttachmentFile implements StreamResponse {
    private byte[] document;
    private String attachmentName;

    public AttachmentFile( byte[] document, String attachmentName ) {
        this.document = document;
        this.attachmentName = attachmentName;
    }

    @Override
    public InputStream getStream() throws IOException {
        return new ByteArrayInputStream( document );
    }

    @Override
    public void prepareResponse( Response response ) {
        response.setHeader("Content-Disposition", "attachment; filename=" + attachmentName );
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentLength( document.length );
    }

    String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName( String attachmentName ) {
        this.attachmentName = attachmentName;
    }
}
