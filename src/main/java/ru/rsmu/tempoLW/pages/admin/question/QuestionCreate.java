package ru.rsmu.tempoLW.pages.admin.question;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextArea;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ValueEncoderSource;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.data.QuestionType;
import ru.rsmu.tempoLW.entities.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author leonid.
 */
@RequiresRoles(value = {"admin","subject_admin"}, logical = Logical.OR )
public class QuestionCreate {
    @Property
    @PageActivationContext
    private ExamSubject subject;

    @Property
    private SelectModel topicModel;

    @Property
    private SubTopic topic;

    @Property
    private int complexity;

    @Property
    private int maxScore;

    @Property
    private String name;

    @Property
    private String text;

    @Property
    private QuestionType type;

    @Inject
    private SelectModelFactory modelFactory;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private LinkSource linkSource;

    @Inject
    private Messages messages;

    @InjectComponent
    private Form questionForm;

    @InjectComponent
    private TextArea questionText;

    public void onActivate() {
        List<SubTopic> topicList = questionDao.findTopicsOfSubject( subject );
        topicModel = modelFactory.create( topicList, "title" );
    }

    public ValueEncoder<SubTopic> getTopicEncoder() {
        return valueEncoderSource.getValueEncoder( SubTopic.class );
    }

    public void onValidateFromQuestionForm() {
        if ( StringUtils.isBlank( text ) ) {
            questionForm.recordError( questionText, messages.get( "search-form-empty" ) );
        }

    }

    public Object onSuccess() {
        QuestionInfo questionInfo = new QuestionInfo();
        questionInfo.setComplexity( complexity );
        questionInfo.setMaxScore( maxScore );
        if ( StringUtils.isNotBlank( name ) ) {
            questionInfo.setName( name );
        }
        else {
            questionInfo.setName( topic.getTitle() );
        }
        questionInfo.setTopic( topic );
        questionInfo.setSubject( subject );

        Question question = null;
        switch ( type ) {
            case OPEN:
                question = new QuestionOpen();
                break;
            case TREE:
                question = new QuestionTree();
                break;
            case SIMPLE_ORDER:
                question = new QuestionSimpleOrder();
                break;
            case SIMPLE:
                question = new QuestionSimple();
                break;
            case CORRESPONDENCE:
                question = new QuestionCorrespondence();
                break;
        }
        question.setCreatedDate( new Date() );
        question.setQuestionInfo( questionInfo );
        question.setText( text );

        questionDao.save( questionInfo );
        questionDao.save( question );

        return linkSource.createPageRenderLink( "admin/question/" + QuestionEdit.class.getSimpleName(), false, question );
    }

    public Map<String, Object> getSubjectParams() {
        Map<String, Object> params = new HashMap<>();
        params.put( "subjectId", subject.getId() );
        params.put( "mode", "REVIEW" );
        return params;
    }

}
