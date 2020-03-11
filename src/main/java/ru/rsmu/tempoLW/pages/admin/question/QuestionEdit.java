package ru.rsmu.tempoLW.pages.admin.question;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.upload.services.UploadedFile;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.pages.QuestionImage;
import ru.rsmu.tempoLW.pages.admin.PreviewQuestion;
import ru.rsmu.tempoLW.pages.admin.Subjects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author leonid.
 */
@RequiresRoles(value = {"admin","subject_admin"}, logical = Logical.OR )
public class QuestionEdit {
    @Property
    @PageActivationContext
    private Long questionId;

    @Property
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
    Block questionSimple, questionOpen, questionCorrespondence, questionSimpleOrder, questionTree;

    public Block getQuestionBlock() {
        if ( question instanceof QuestionSimple ) {
            return questionSimple;
        }
        else if ( question instanceof QuestionOpen ) {
            return questionOpen;
        }
        else if ( question instanceof QuestionCorrespondence ) {
            return questionCorrespondence;
        }
        else if ( question instanceof QuestionSimpleOrder ) {
            return questionSimpleOrder;
        }
        else if ( question instanceof QuestionTree ) {
            return questionTree;
        }
        return null;
    }

    public Object onActivate() {
        return prepare();
    }

    private Object prepare() {
        if ( questionId != null ) {
            question = questionDao.find( Question.class, questionId );
            if ( question != null ) {
                topicList = questionDao.findTopicsOfSubject( question.getQuestionInfo().getSubject() );
                topicModel = modelFactory.create( topicList, "title" );

                return null;
            }
        }
        return Subjects.class;
    }

    public Object onSuccess() {
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
        questionDao.save( question );
        return linkSource.createPageRenderLink( "admin/" + PreviewQuestion.class.getSimpleName(), false, question.getQuestionInfo().getSubject().getId(), question.getId() );
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
        return new ValueEncoder<SubTopic>() {
            @Override
            public String toClient( SubTopic value ) {
                return String.valueOf( value.getId() );
            }

            @Override
            public SubTopic toValue( String clientValue ) {
                long id = Long.parseLong( clientValue );
                for ( SubTopic topic : topicList ) {
                    if ( topic.getId() == id ) {
                        return topic;
                    }
                }
                return null;
            }
        };
    }

    public boolean isQuestionSimple() {
        return question instanceof QuestionSimple;
    }

    public boolean isQuestionOpen() {
        return question instanceof QuestionOpen;
    }

    public boolean isQuestionCorrespondence() {
        return question instanceof QuestionCorrespondence;
    }

    public boolean isQuestionSimpleOrder() {
        return question instanceof QuestionSimpleOrder;
    }

    public boolean isQuestionTree() { return  question instanceof QuestionTree; }

    public String getImageLink() {
        if ( question.getImage() != null ) {
            return linkSource.createPageRenderLink(QuestionImage.class.getSimpleName(), false, question.getImage().getId() ).toURI();
        }
        return "";
    }
}
