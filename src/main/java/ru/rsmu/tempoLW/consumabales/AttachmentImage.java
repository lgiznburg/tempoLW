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
public class AttachmentImage implements StreamResponse {
    private UploadedImage uploadedImage;

    public AttachmentImage( UploadedImage uploadedImage ) {
        this.uploadedImage = uploadedImage;
    }

    @Override
    public String getContentType() {
        return uploadedImage.getContentType();
    }

    @Override
    public InputStream getStream() throws IOException {
        return new ByteArrayInputStream( uploadedImage.getPicture() );
    }

    @Override
    public void prepareResponse( Response response ) {
        String extension = getExtension();
        response.setHeader("Content-Disposition", "attachment; filename=questionimage" + uploadedImage.getId() + (!extension.isEmpty()? ("." + extension) : "") );
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentLength( uploadedImage.getPicture().length );
    }

    private String getExtension() {
        Pattern pattern = Pattern.compile( "\\.(\\d+)$" );
        Matcher matcher = pattern.matcher( uploadedImage.getSourceName() );
        if ( matcher.find() ) {
            return matcher.group(1);
        }
        return "";
    }
}
