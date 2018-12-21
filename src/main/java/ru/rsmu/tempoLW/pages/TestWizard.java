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
public class TestWizard {

    @Property
    @PageActivationContext
    private int questionNumber;

    @Property
    @SessionState
    private TestResult testResult;

    @Inject
    private QuestionDao questionDao;

    @Property
    private QuestionResult current;

    @Inject
    @Path("context:/static/js/MathJax/MathJax.js")
    private Asset mathJax;

    @Inject
    @Path( "context:/static/js/myMathJaxConfig.js" )
    private Asset mathJaxConfig;

    @Environmental
    private JavaScriptSupport javaScriptSupport;

    public void setupRender() {
        javaScriptSupport.importJavaScriptLibrary( mathJaxConfig );
        javaScriptSupport.importJavaScriptLibrary( mathJax );
    }

    public Object onActivate() {
        if ( testResult == null || testResult.getQuestionResults() == null ) {
            return Index.class;
        }
        if ( questionNumber < 0 ||
                questionNumber >= testResult.getQuestionResults().size() ) {
            // check out of bounds
            questionNumber = 0;
        }
        current = testResult.getQuestionResults().get( questionNumber );
        if ( current.getElements() == null ) {
            current.setElements( new LinkedList<>() );
        }
        return null;
    }

    public Object onSuccess() {
        current.checkCorrectness();

        //todo save

        if ( testResult.getQuestionResults().size() -1 == questionNumber ) {
            return TestFinal.class;
        }
        return onNextQuestion();
    }

    public Object onNextQuestion() {
        if ( testResult.getQuestionResults().size()-1 > questionNumber ) {
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
        return questionNumber < (testResult.getQuestionResults().size() - 1);
    }

    public boolean isPrevExist() {
        return questionNumber > 0;
    }

    /*public void setQuestionNumber( int questionNumber ) {
        this.questionNumber = questionNumber;
    }*/
}
