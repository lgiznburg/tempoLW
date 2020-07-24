package ru.rsmu.tempoLW.components.admin;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.*;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.LocalizationSetter;
import org.apache.tapestry5.services.ValueEncoderSource;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.apache.tapestry5.util.AbstractSelectModel;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * @author leonid.
 */
public class SubjectEdit {
    @Property
    @Parameter(required = true)
    private Long subjectId;

    @Property
    private ExamSubject subject;

    @Property
    private String additionalTopicName;

    @Property
    private List<SubTopic> topics;

    /**
     * for iteration through topics list
     */
    @Property
    private SubTopic topic;

    /**
     * object for iteration through subject.referenceMaterials
     */
    @Property
    private SubjectReferenceMaterial referenceMaterial;

    /**
     * for upload additional materials
     */
    @Property
    private UploadedFile imageFile;

    @Property
    private String materialsTitle;

    @Inject
    private QuestionDao questionDao;

    @InjectComponent("subjectForm")
    private Form subjectForm;

    @Inject
    private ComponentResources componentResources;

    @Inject
    private LocalizationSetter localizationSetter;

    @Inject
    private Locale currentLocale;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    public void onPrepareForRender() {
        if ( subjectForm.isValid() ) {
            prepare();
        }
    }

    public void onPrepareForSubmit() {
        prepare();
    }

    private void prepare() {
        topics = new ArrayList<>();
        if ( subjectId != null ) {
            subject = questionDao.find( ExamSubject.class, subjectId );
        }
        if ( subject == null ) {
            subject = new ExamSubject();
            subject.setType( SubjectType.UNDEFINED );
        }
        else {
            topics = questionDao.findTopicsOfSubject( subject );
        }
    }

    public ValueEncoder<SubTopic> getSubTopicEncoder() {
        return valueEncoderSource.getValueEncoder( SubTopic.class );
    }

    public boolean isTopicDeletable() {
        return topic.getQuestionsCount() == 0;
    }

    public void onDeleteTopic( SubTopic subTopic ) {
        try {
            questionDao.delete( subTopic );
        } catch (Exception e) {
            subjectForm.recordError( "Impossible to delete the topic." );
        }
    }

    public void onDeleteReference( SubjectReferenceMaterial referenceMaterial ) {
        //subject.getReferenceMaterials().remove( referenceMaterial );
        //UploadedImage image = referenceMaterial.getImage();
        //referenceMaterial.setImage( null );
        ///questionDao.delete( image );
        questionDao.delete( referenceMaterial );
    }

    public boolean onSuccess() {
        boolean stayHere = false;
        // check and add new topic
        if ( StringUtils.isNotBlank( additionalTopicName ) ) {
            SubTopic topic = new SubTopic();
            topic.setSubject( subject );
            topic.setTitle( additionalTopicName );
            questionDao.save( topic );
            stayHere =  true;
        }

        //check and add new reference material
        UploadedImage image = null;
        try {
            if ( imageFile != null && imageFile.getContentType().matches( "image.*" )
                    && StringUtils.isNotBlank( materialsTitle )
            ) {
                image = new UploadedImage();
                image.setContentType( imageFile.getContentType() );
                image.setSourceName( imageFile.getFileName() );
                image.setPicture( IOUtils.toByteArray( imageFile.getStream() ) );
                questionDao.save( image );
                SubjectReferenceMaterial referenceMaterial = new SubjectReferenceMaterial();
                referenceMaterial.setImage( image );
                referenceMaterial.setTitle( materialsTitle );
                referenceMaterial.setSubject( subject );
                questionDao.save( referenceMaterial );
                stayHere = true;
            }
        } catch (IOException e) {
            //
        }

        String event = subject.getId() != 0 ? "subjectUpdated" : "subjectCreated";
        questionDao.save( subject );
        if ( !stayHere ) {
            componentResources.triggerEvent( event, new Object[]{subject.getId()}, null );
        }

        return true;
    }

    public String getTitle() {
        return subject.getId() != 0 ? "Edit" : "Create";
    }

    public SelectModel getLocalesModel() {
        return new AbstractSelectModel() {
            @Override
            public List<OptionGroupModel> getOptionGroups() {
                return null;
            }

            @Override
            public List<OptionModel> getOptions() {
                List<OptionModel> options = new ArrayList<>();
                for ( Locale locale : localizationSetter.getSupportedLocales() ) {
                    options.add( new OptionModelImpl( StringUtils.capitalize( locale.getDisplayLanguage( currentLocale ) ), locale.getLanguage() ) ); }

                return options;
            }
        };
    }
}
