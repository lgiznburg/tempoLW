package ru.rsmu.tempoLW.pages.admin.templates;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;
import ru.rsmu.tempoLW.dao.HibernateModule;
import ru.rsmu.tempoLW.dao.RtfTemplateDao;
import ru.rsmu.tempoLW.entities.DocumentTemplate;
import ru.rsmu.tempoLW.entities.DocumentTemplateType;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * @author polyakov_ps
 */

@Import(module = "templates")
@RequiresRoles(value = "admin")
public class RtfTemplates {

    @Property
    private Boolean modified; //for context. always true

    @Property
    private List<DocumentTemplate> templates;

    @Property
    private DocumentTemplate template; //active template file for manipulations

    @InjectComponent
    private Form commentForm;

    @InjectComponent
    private Form uploadForm;

    @Property
    private UploadedFile uploadedFile;

    @Inject
    private Messages messages;

    @Inject
    private RtfTemplateDao rtfTemplateDao;

    @Inject
    @Property
    private Locale currentLocale; //to use in .tml to call proper ENUM description

    public RtfTemplates() {}

    public void setupRender() {
        //fill the templates list from the DB
        templates = rtfTemplateDao.findAll( DocumentTemplate.class );
        modified = true;
    }

    public void onPrepareForSubmitFromCommentForm( DocumentTemplateType type ) {
        //make sure template is appropriate
        template = rtfTemplateDao.findByType( type );
    }

    public void onSuccessFromCommentForm( DocumentTemplateType type ) {
        saveComment( template.getTemplateComment(), type );
    }

    public void onValidateFromUploadForm() {
        if ( !uploadedFile.getFileName().matches(".*\\.rtf") ) {
            uploadForm.recordError( messages.get( "rtf-template-error" ) ); //need to debug (doesn't really validate for some reason!)
        }
    }

    public void onPrepareForSubmitFromUploadForm( DocumentTemplateType type ) {
        //make sure template is appropriate
        template = rtfTemplateDao.findByType( type );
    }

    public void onSuccessFromUploadForm() {
        saveUploadedTemplate();
    }

    //discard modified template and use default from resources
    public void onResetTemplate( DocumentTemplateType type ) {

        InputStream is = HibernateModule.class.getClassLoader().getResourceAsStream( "template/" + type.name().toLowerCase() + ".rtf" );
        if ( is != null ) {
            try {
                DocumentTemplate template = rtfTemplateDao.findByType( type );
                template.setModified( false );
                template.setRtfTemplate( IOUtils.toString( is ) );
                template.setFileName( type.name().toLowerCase() + ".rtf" );
                rtfTemplateDao.save( template );
            } catch ( IOException ie ) {
                //ok
            }
        }

    }

    //save uploaded template (from the properties of this page)
    private void saveUploadedTemplate () {

        //check if something is submitted and that it's an RTF file
        try {
            //check if the file is of the right format
            if (uploadForm.isValid() && uploadedFile.getFileName().matches(".*\\.rtf")) {
                //DocumentTemplate template = rtfTemplateDao.findByType( type );
                String uploadedTemplate = IOUtils.toString( uploadedFile.getStream() );
                //check if uploaded file is different from existing. If yes, change modified flag and upload file. - MD5 checksum

                if ( !DigestUtils.md5Hex(template.getRtfTemplate()).equals(DigestUtils.md5Hex(uploadedTemplate)) ) {
                    template.setModified(true);
                    template.setFileName( uploadedFile.getFileName() );
                    template.setRtfTemplate( uploadedTemplate );
                    rtfTemplateDao.save(template);
                }
            }
        } catch ( Exception e ) {
            //
        }

    }

    private void saveComment ( String comment, DocumentTemplateType type ) {

            DocumentTemplate template = rtfTemplateDao.findByType( type );
            template.setTemplateComment( comment );
            rtfTemplateDao.save(template);

    }
}
