package ru.rsmu.tempoLW.consumabales;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.services.Response;
import ru.rsmu.tempoLW.entities.UploadedImage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author leonid.
 */
public class AttachmentImage extends AttachmentFile {
    private String contentType;

    public AttachmentImage( UploadedImage uploadedImage ) {
        super( uploadedImage.getPicture(), "" );
        String extension = getExtension( uploadedImage );
        setAttachmentName( "questionimage" + uploadedImage.getId() + (!extension.isEmpty()? ("." + extension) : "") );
        contentType = uploadedImage.getContentType();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    private String getExtension( UploadedImage uploadedImage ) {
        Pattern pattern = Pattern.compile( "\\.(\\d+)$" );
        Matcher matcher = pattern.matcher( uploadedImage.getSourceName() );
        if ( matcher.find() ) {
            return matcher.group(1);
        }
        return "";
    }
}
