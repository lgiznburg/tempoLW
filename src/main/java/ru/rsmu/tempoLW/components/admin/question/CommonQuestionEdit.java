package ru.rsmu.tempoLW.components.admin.question;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ComponentAction;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.FormSupport;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ValueEncoderSource;
import org.apache.tapestry5.upload.services.UploadedFile;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.entities.SubTopic;
import ru.rsmu.tempoLW.entities.UploadedImage;
import ru.rsmu.tempoLW.pages.QuestionImage;

import java.io.IOException;
import java.util.List;

/**
 * @author leonid.
 */
public class CommonQuestionEdit {

    @Property
    @Parameter(required = true, allowNull = false)
    private Question question;

    @Property
    private SelectModel topicModel;

    @Property
    private UploadedFile imageFile;

    @Inject
    private SelectModelFactory modelFactory;

    @Inject
    private QuestionDao questionDao;

    private List<SubTopic> topicList;

    @Inject
    private LinkSource linkSource;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    @Environmental
    private FormSupport formSupport;

    void setupRender() {
        topicList = questionDao.findTopicsOfSubject( question.getQuestionInfo().getSubject() );
        topicModel = modelFactory.create( topicList, "title" );
    }

    private static final ProcessSubmissionAfter PROCESS_SUBMISSION_AFTER = new ProcessSubmissionAfter();

    // Tapestry calls afterRender() AFTER it renders any components I contain (ie. Loop).
    final void afterRender() {

        // If we are inside a form, ask FormSupport to store PROCESS_SUBMISSION_AFTER in its list of actions to do on
        // submit. If there are other components, their actions will already be in the list, before
        // PROCESS_SUBMISSION_AFTER. That is because this method, afterRender(), is late in the sequence. This
        // guarantees PROCESS_SUBMISSION_AFTER will be executed on submit AFTER the components are processed
        // (which includes their validation).

        if (formSupport != null) {
            formSupport.store(this, PROCESS_SUBMISSION_AFTER);
        }
    }

    private static class ProcessSubmissionAfter implements ComponentAction<CommonQuestionEdit> {
        private static final long serialVersionUID = 1L;

        public void execute(CommonQuestionEdit component) {
            component.processSubmissionAfter();
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + ".ProcessSubmissionAfter";
        }
    };

    protected void processSubmissionAfter() {
        UploadedImage image = null;
        try {
            if ( imageFile != null && imageFile.getContentType().matches( "image.*" ) ) {
                image = question.getImage() == null ? new UploadedImage() : question.getImage();
                image.setContentType( imageFile.getContentType() );
                image.setSourceName( imageFile.getFileName() );
                image.setPicture( IOUtils.toByteArray( imageFile.getStream() ) );
            }
        } catch (IOException e) {
            //
        }
        if ( question.getImage() == null && image != null ) {
            questionDao.save( image );
            question.setImage( image );
        }
    }

    public Object onDeleteImage() {
        if ( question.getImage() != null ) {
            UploadedImage image = question.getImage();
            question.setImage( null );
            questionDao.save( question );
            questionDao.delete( image );
        }
        return this;
    }

    public ValueEncoder<SubTopic> getTopicEncoder() {
        return valueEncoderSource.getValueEncoder( SubTopic.class );
    }

    public String getImageLink() {
        if ( question.getImage() != null ) {
            return linkSource.createPageRenderLink( QuestionImage.class.getSimpleName(), false, question.getImage().getId() ).toURI();
        }
        return "";
    }

    public boolean getImagePresent() {
        return question.getImage() != null;
    }
}
