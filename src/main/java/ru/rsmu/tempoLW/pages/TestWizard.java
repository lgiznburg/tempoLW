package ru.rsmu.tempoLW.pages;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.services.SecurityUserHelper;

import java.util.Calendar;
import java.util.Date;
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

    @Inject
    private ExamDao examDao;

    @Inject
    private SecurityUserHelper securityUserHelper;

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
        Testee testee = securityUserHelper.getCurrentTestee();
        if ( testee != null && examResult.getTestee().getId() != testee.getId() ) {
            return Index.class;
        }
        if ( examResult.getId() > 0 ) {
            //proof lazy init exception
            examDao.refresh( examResult );
        }
        if ( examResult.getStartTime() == null ) {
            //just started
            examResult.setStartTime( new Date() );
            if ( examResult.getId() > 0 ) {
                examDao.save( examResult );
            }
        }
        else if ( examResult.isExamMode() && getEstimatedEndTime().before( new Date() ) ) {
            // check time - if testee used "Next/Prev question" button
            examResult.setEndTime( new Date() );
            examDao.save( examResult );
            return TestFinal.class;
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

        //save only existed result
        if ( examResult.getId() > 0 ) {
            if ( getEstimatedEndTime().before( new Date() ) ) {
                examResult.setEndTime( new Date() );
            }
            examDao.save( examResult );
        }

        if ( examResult.getQuestionResults().size() -1 == questionNumber || examResult.isFinished()) {
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

    public Date getEstimatedEndTime() {
        Calendar calendar = Calendar.getInstance();
        if ( examResult.isExamMode() ) {
            calendar.setTime( examResult.getStartTime() );
            if ( examResult.getExam().getDurationHours() > 0 ) {
                calendar.add( Calendar.HOUR, examResult.getExam().getDurationHours() );
            }
            if ( examResult.getExam().getDurationMinutes() > 0 ) {
                calendar.add( Calendar.MINUTE, examResult.getExam().getDurationMinutes() );
            }
        }
        return calendar.getTime();
    }
}
