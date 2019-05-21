package ru.rsmu.tempoLW.consumabales;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.services.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author leonid.
 */
public class AttachmentRtf  extends AttachmentFile {

    public AttachmentRtf( byte[] document, String attachmentName ) {
        super(document, attachmentName);
    }

    @Override
    public String getContentType() {
        return "text/rtf";
    }

}