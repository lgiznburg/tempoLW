package ru.rsmu.tempoLW.pages;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;

import java.util.LinkedList;

/**
 * @author leonid.
 */
@Import( stylesheet = {"katex/katex.css"})
public class TestWizard {

    @Property
    @PageActivationContext
    private int questionNumber;

    @Property
    @SessionState
    private ExamResult examResult;

    @Inject
    private QuestionDao questionDao;

    @Property
    private QuestionResult current;

    @Environmental
    private JavaScriptSupport javaScriptSupport;

    public void setupRender() {

        javaScriptSupport.require( "katex/katex" );
        javaScriptSupport.require( "katex/contrib/auto-render" ).invoke( "renderMathInElementOfClass" ).with( "container" );
    }

    public Object onActivate() {
        if ( examResult == null || examResult.getQuestionResults() == null ) {
            return Index.class;
        }
        if ( questionNumber < 0 ||
                questionNumber >= examResult.getQuestionResults().size() ) {
            // check out of bounds
            questionNumber = 0;
        }
        current = examResult.getQuestionResults().get( questionNumber );
        if ( current.getElements() == null ) {
            current.setElements( new LinkedList<>() );
        }
        return null;
    }

    public Object onSuccess() {
        current.checkCorrectness();

        //todo save

        if ( examResult.getQuestionResults().size() -1 == questionNumber ) {
            return TestFinal.class;
        }
        return onNextQuestion();
    }

    public Object onNextQuestion() {
        if ( examResult.getQuestionResults().size()-1 > questionNumber ) {
            questionNumber++;
        }
        return this;
    }

    public Object onPrevQuestion() {
        if ( questionNumber > 0 ) {
            questionNumber--;
        }
        return this;
    }

    public boolean isNextExist() {
        return questionNumber < (examResult.getQuestionResults().size() - 1);
    }

    public boolean isPrevExist() {
        return questionNumber > 0;
    }

    /*public void setQuestionNumber( int questionNumber ) {
        this.questionNumber = questionNumber;
    }*/
}
