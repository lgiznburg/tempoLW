package ru.rsmu.tempoLW.pages.admin.question;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ValueEncoderSource;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.hibernate.Session;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.datasource.QuestionsDataSource;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.pages.admin.Subjects;

import java.util.List;

/**
 * @author leonid.
 */
@RequiresRoles(value = {"admin","subject_admin","teacher"}, logical = Logical.OR )
public class QuestionList {
    @Property
    @PageActivationContext
    private ExamSubject subject;

    //
    @Property
    @SessionState
    private SubTopic topic;

    @Property
//    @SessionState
    private Integer complexity;

    @Property
//    @SessionState
    private Integer maxScore;

    @Property
//    @SessionState
    private String text;

    /**
     * Property for interaction through the grid
     */
    @Property
    private Question question;

    //=============
    @Inject
    private QuestionDao questionDao;

    @Inject
    private SelectModelFactory modelFactory;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    @Inject
    private Session session;

    @InjectComponent
    private Zone questionGridZone;

    @Inject
    private Request request;

    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;

    @InjectPage
    private Subjects subjectsPage;

    void onActivate() {
        if ( topic == null || topic.getSubject() == null ||
            !topic.getSubject().equals( subject ) ) {
            List<SubTopic> topics = questionDao.findTopicsOfSubject( subject );
            if ( topics.size() > 0 ) {
                topic = topics.get( 0 );
            }
            //complexity = null;
            //maxScore = null;
            text = null;
        }
    }

    public void set( ExamSubject subject ) {
        this.subject = subject;
    }

    public ValueEncoder<SubTopic> getTopicEncoder() {
        return valueEncoderSource.getValueEncoder( SubTopic.class );
    }

    public SelectModel getTopicModel() {
        return modelFactory.create( questionDao.findTopicsOfSubject( subject ), "title" );
    }

    public QuestionsDataSource getQuestionDataSource() {
        return new QuestionsDataSource(session, Question.class, subject, topic, complexity, maxScore, text );
    }

    public void onValidateFromQuestionSearch() {
        if (request.isXHR()) {
            ajaxResponseRenderer.addRender(questionGridZone);
        }
    }

    public void onValueChangedFromTopic() {
        if (request.isXHR()) {
            ajaxResponseRenderer.addRender(questionGridZone);
        }
    }

    public Object onGoToSubject() {
        subjectsPage.set( subject.getId() );
        return subjectsPage;
    }

    public String getQuestionText() {
        StringBuilder stringBuilder = new StringBuilder( question.getText() );
        if ( question instanceof QuestionSimple ) {
            QuestionSimple simple = (QuestionSimple) question;
            stringBuilder.append( "<ul><li>" );
            for ( AnswerVariant answerVariant : simple.getAnswerVariants() ) {
                if ( answerVariant.isCorrect() ) {
                    stringBuilder.append( answerVariant.getReadableText() );
                    break;
                }
            }
            stringBuilder.append( "</li></ul>" );
        }
        else if ( question instanceof QuestionOpen ) {
            QuestionOpen open = (QuestionOpen) question;
            stringBuilder.append( "<ul><li>" );
            for ( AnswerVariant answerVariant : open.getAnswerVariants() ) {
                stringBuilder.append( answerVariant.getReadableText() );
            }
            stringBuilder.append( "</li></ul>" );
        }
        else if ( question instanceof QuestionCorrespondence ) {
            QuestionCorrespondence correspondence = (QuestionCorrespondence) question;
            stringBuilder.append( "<dl><dt>" );
            CorrespondenceVariant correspondenceVariant = correspondence.getCorrespondenceVariants().get( 0 );
            stringBuilder.append( correspondenceVariant.getText() )
            .append( "</dt><dd>" );

            for ( AnswerVariant answerVariant : correspondenceVariant.getCorrectAnswers() ) {
                if ( answerVariant.isCorrect() ) {
                    stringBuilder.append( answerVariant.getReadableText() );
                    break;
                }
            }
            stringBuilder.append( "</dd></dl>" );
        }
        else if ( question instanceof QuestionSimpleOrder ) {
            QuestionSimpleOrder order = (QuestionSimpleOrder) question;
            stringBuilder.append( "<ol><li>" );
            for ( AnswerVariant answerVariant : order.getAnswerVariants() ) {
                if ( answerVariant.isCorrect() ) {
                    stringBuilder.append( answerVariant.getReadableText() );
                    break;
                }
            }
            stringBuilder.append( "</li></ol>" );
        }
        else if ( question instanceof QuestionTree ) {
            QuestionTree tree = (QuestionTree) question;
            stringBuilder.append( "<dl><dt>" );
            CorrespondenceVariant correspondenceVariant = tree.getCorrespondenceVariants().get( 0 );
            stringBuilder.append( correspondenceVariant.getText() )
                    .append( "</dt><dd>" );

            for ( AnswerVariant answerVariant : correspondenceVariant.getCorrectAnswers() ) {
                if ( answerVariant.isCorrect() ) {
                    stringBuilder.append( answerVariant.getReadableText() );
                    break;
                }
            }
            stringBuilder.append( "</dd></dl>" );
        }
        return stringBuilder.toString();
    }
}
