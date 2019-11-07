package ru.rsmu.tempoLW.pages.admin.templates;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;
import ru.rsmu.tempoLW.dao.HibernateModule;
import ru.rsmu.tempoLW.dao.RtfTemplateDao;
import ru.rsmu.tempoLW.entities.DocumentTemplate;
import ru.rsmu.tempoLW.entities.DocumentTemplateType;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author polyakov_ps
 */

@Import(module = "templates")
@RequiresRoles(value = "admin")
public class RtfTemplates {

    @Property
    private Boolean modified;

    //exam result template
    @Property
    private DocumentTemplate examResult; //template to display

    @Property
    private UploadedFile examResultUploaded; //file to upload template

    @InjectComponent
    private Form examResultUploadForm; //upload form

    @InjectComponent
    private Form examResultCommentForm; //comment editor form

    @Property
    private String examResultComment; //comment

    //exam statement template
    @Property
    private DocumentTemplate examStatement; //template to display

    @Property
    private UploadedFile examStatementUploaded; //file to upload template

    @InjectComponent
    private Form examStatementUploadForm; //upload form

    @InjectComponent
    private Form examStatementCommentForm; //comment editor form

    @Property
    private String examStatementComment; //comment

    //login records template
    @Property
    private DocumentTemplate logins; //template to display

    @Property
    private UploadedFile loginsUploaded; //file to upload template

    @InjectComponent
    private Form loginsUploadForm; //upload form

    @InjectComponent
    private Form loginsCommentForm; //comment editor form

    @Property
    private String loginsComment; //comment

    @Inject
    private RtfTemplateDao rtfTemplateDao;

    public void setupRender() {

        //loading current templates to display on page
        modified = true;
        examResult = rtfTemplateDao.findByType( DocumentTemplateType.EXAM_RESULT );
        examStatement = rtfTemplateDao.findByType( DocumentTemplateType.EXAM_STATEMENT );
        logins = rtfTemplateDao.findByType( DocumentTemplateType.LOGINS );

        examResultComment = examResult.getTemplateComment();
        examStatementComment = examStatement.getTemplateComment();
        loginsComment = logins.getTemplateComment();

    }

    public void onSuccessFromExamResultUploadForm() {
        saveUploadedTemplate( examResultUploadForm, examResultUploaded, DocumentTemplateType.EXAM_RESULT );
    }

    public void onSuccessFromExamStatementUploadForm() {
        saveUploadedTemplate( examStatementUploadForm, examStatementUploaded, DocumentTemplateType.EXAM_STATEMENT );
    }

    public void onSuccessFromLoginsUploadForm() {
        saveUploadedTemplate( loginsUploadForm, loginsUploaded, DocumentTemplateType.LOGINS );
    }

    public void onSuccessFromExamResultCommentForm() {
        saveComment( examResultComment, DocumentTemplateType.EXAM_RESULT );
    }

    public void onSuccessFromExamStatementCommentForm() {
        saveComment( examStatementComment, DocumentTemplateType.EXAM_STATEMENT );
    }

    public void onSuccessFromLoginsCommentForm() {
        saveComment( loginsComment, DocumentTemplateType.LOGINS );
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

    private void saveUploadedTemplate ( Form form, UploadedFile file, DocumentTemplateType type ) {

        //check if something is submitted and that it's an RTF file
        try {
            if (form.isValid() && file.getFileName().matches(".*\\.rtf")) {
                DocumentTemplate template = rtfTemplateDao.findByType( type );

                //check if uploaded file is different from existing. If yes, change modified flag and upload file.
                if (!template.getRtfTemplate().equals( IOUtils.toString( file.getStream() ) ) ) {
                    template.setModified(true);
                    template.setFileName( file.getFileName() );
                    template.setRtfTemplate( IOUtils.toString( file.getStream() ) );
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
