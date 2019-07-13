package ru.rsmu.tempoLW.consumabales;

/**
 * @author leonid.
 */
public class AttachmentPlainText extends AttachmentFile {

    public AttachmentPlainText( byte[] document, String attachmentName ) {
        super( document, attachmentName );
    }

    @Override
    public String getContentType() {
        return "text/plain";
    }
}
