package ru.rsmu.tempoLW.pages;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.consumabales.AttachmentImage;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.UploadedImage;

/**
 * @author leonid.
 */
public class QuestionImage {
    @Property
    @PageActivationContext
    private Long id;

    @Inject
    private LinkSource linkSource;

    @Inject
    private QuestionDao questionDao;

    public String getUploadedLink(Long id) {
        return linkSource.createPageRenderLink(QuestionImage.class.getSimpleName(), false, id ).toURI();
    }

    public StreamResponse onActivate() {
        UploadedImage image = questionDao.find( UploadedImage.class, id );
        return new AttachmentImage( image );
    }
}
