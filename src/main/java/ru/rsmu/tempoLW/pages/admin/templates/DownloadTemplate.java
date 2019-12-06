package ru.rsmu.tempoLW.pages.admin.templates;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.consumabales.AttachmentRtf;
import ru.rsmu.tempoLW.dao.HibernateModule;
import ru.rsmu.tempoLW.dao.RtfTemplateDao;
import ru.rsmu.tempoLW.entities.DocumentTemplate;
import ru.rsmu.tempoLW.entities.DocumentTemplateType;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author polyakov_ps
 */

public class DownloadTemplate {

    @PageActivationContext
    private DocumentTemplateType documentTemplateType;

    @PageActivationContext( index = 1 )
    private Boolean modified;

    @Inject
    private RtfTemplateDao rtfTemplateDao;

    public StreamResponse onActivate() throws IOException {
        if ( modified == null ) { modified = false; }
        DocumentTemplate template = new DocumentTemplate();

        if ( modified ) {
            template = rtfTemplateDao.findByType(documentTemplateType); //returns current template whether it's modified or not
        } else {
            //getting template from resources
            template.setTemplateType( documentTemplateType );
            InputStream is = HibernateModule.class.getClassLoader().getResourceAsStream( "template/" + documentTemplateType.name().toLowerCase() + ".rtf" );
            if ( is != null ) {
                try {
                    template.setRtfTemplate( IOUtils.toString( is ) );
                } catch ( IOException ie ) {
                    //it's fine, just ignore it
                }
            }
        }

        return new AttachmentRtf( template.getRtfTemplate().getBytes(), modified ? template.getFileName() : documentTemplateType.toString().toLowerCase() + "_default.rtf" );

    }
}
