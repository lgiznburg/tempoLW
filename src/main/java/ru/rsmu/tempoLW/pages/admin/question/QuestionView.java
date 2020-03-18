package ru.rsmu.tempoLW.pages.admin.question;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ValueEncoderSource;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.entities.SubTopic;
import ru.rsmu.tempoLW.pages.admin.Subjects;


/**
 * @author leonid.
 */
@Import( stylesheet = {"katex/katex.css"})
@RequiresRoles(value = {"admin","subject_admin","teacher"}, logical = Logical.OR )
public class QuestionView {

    @Property
    @PageActivationContext
    private Question question;

    @Property
    private ExamSubject subject;

    @Property
    private SelectModel topicModel;

    @Inject
    private SelectModelFactory modelFactory;

    @Inject
    private QuestionDao questionDao;

    @Environmental
    private JavaScriptSupport javaScriptSupport;

    @InjectPage
    private QuestionList questionList;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    public void setupRender() {

        javaScriptSupport.require( "katex/katex" );
        javaScriptSupport.require( "katex/contrib/auto-render" ).invoke( "renderMathInElementOfClass" ).with( "container" );
    }

    public Object  onActivate() {
        if ( question == null ) {
            return Subjects.class;
        }

        subject = question.getQuestionInfo().getSubject();

        return null;
    }

    public Object onNextQuestion() {
        Question next = questionDao.findNextQuestion( question );
        if ( next == null ) {
            questionList.set( subject );
            return questionList;
        }
        question = next;
        return this;
    }


    public Object onPrevQuestion() {
        Question prev = questionDao.findPrevQuestion( question );
        if ( prev == null ) {
            questionList.set( subject );
            return questionList;
        }
        question = prev;
        return this;
    }

    public Object onToQuestionList() {
        questionList.set( question.getQuestionInfo().getSubject() );
        return questionList;
    }

}
