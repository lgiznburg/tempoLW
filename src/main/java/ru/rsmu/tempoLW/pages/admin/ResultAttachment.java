package ru.rsmu.tempoLW.pages.admin;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.consumabales.AttachmentFile;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ResultAttachedFile;

/**
 * @author leonid.
 */
public class ResultAttachment {
    @Property
    @PageActivationContext
    private Long id;

    @Inject
    private LinkSource linkSource;

    @Inject
    private QuestionDao questionDao;

    public String getUploadedLink(Long id) {
        return linkSource.createPageRenderLink( ResultAttachment.class.getSimpleName(), false, id ).toURI();
    }

    public StreamResponse onActivate() {
        ResultAttachedFile image = questionDao.find( ResultAttachedFile.class, id );

        return new AttachmentFile( image.getContent(), image.getSourceName() ) {
            @Override
            public String getContentType() {
                return image.getContentType();
            }
        };
    }
}
