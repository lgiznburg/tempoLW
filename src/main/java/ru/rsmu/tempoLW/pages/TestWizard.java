package ru.rsmu.tempoLW.pages;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.ajax.JavaScriptCallback;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.hibernate.Hibernate;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.dao.SystemPropertyDao;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.entities.system.StoredPropertyName;
import ru.rsmu.tempoLW.services.SecurityUserHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

/**
 * @author leonid.
 */
@Import( stylesheet = {"katex/katex.css"})
public class TestWizard {

    //@Property
    //private int questionNumber;

    @Property
    @SessionState
    private ExamResult examResult;

    /**
     * Current question in the test.
     */
    @Property
    private QuestionResult current;

    /**
     * What to show on the page - question or result table (final results)
      */
    private ShowMode showMode = ShowMode.SHOW_QUESTION;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private ExamDao examDao;

    @Inject
    private SystemPropertyDao propertyService;

    @Inject
    private SecurityUserHelper securityUserHelper;

    /**
     * Switch blocks on the page
     */
    @Inject
    private Block questionBlock, questionListBlock, startProctoring;

    /**
     * Request for handling AJAX async requests
     */
    @Inject
    private Request request;

    @InjectComponent
    private Zone questionFormZone, examTimingZone;

    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;

    @Environmental
    private JavaScriptSupport javaScriptSupport;

    public void setupRender() {

        javaScriptSupport.require( "katex/katex" );
        javaScriptSupport.require( "katex/contrib/auto-render" ).invoke( "renderMathInElementOfClass" ).with( "container" );

        if ( examResult.getExam() != null && examResult.getExam().isUseProctoring() ) {
            String proctoringJS = propertyService.getProperty( StoredPropertyName.PROCTORING_JS_URL );
            String proctoringServer = propertyService.getProperty( StoredPropertyName.PROCTORING_SERVER_ADDRESS );
//            javaScriptSupport.importJavaScriptLibrary( proctoringJS );
            /*javaScriptSupport.addModuleConfigurationCallback(
                    new ModuleConfigurationCallback() {
                        @Override
                        public JSONObject configure( JSONObject configuration ) {
                            JSONObject paths = new JSONObject().put( "proctorEdu", proctoringJS );
                            configuration.put( "paths", paths );
                            return configuration;
                        }
                    }
            );*/
            javaScriptSupport.require( "proctoring" ).invoke( "startSession" ).with( proctoringServer );
        }
    }

    public Object onActivate() {
        if ( !request.isXHR() ) {   // work with normal call ( not AJAX)
            if ( examResult == null || examResult.getQuestionResults() == null ) {
                return Index.class;  // no exam
            }
            Testee testee = securityUserHelper.getCurrentTestee();
            if ( testee != null && (examResult.getTestee() == null || examResult.getTestee().getId() != testee.getId()) ) {
                return Index.class;  // exam and testee do not match
            }
            if ( examResult.isFinished() ) {  // protect from changing results after exam finish
                //return TestFinal.class;
                showMode = ShowMode.SHOW_TABLE;
            }
            if ( examResult.getId() > 0 ) {
                //proof lazy init exception
                if ( !Hibernate.isInitialized( examResult.getQuestionResults() ) ) {
                    examDao.refresh( examResult );
                }
            }
            if ( examResult.getStartTime() == null ) {
                //just started
                if ( examResult.getExam() != null && examResult.getExam().isUseProctoring() ) {
                    showMode = ShowMode.START_TEST;  // start test after proctoring init
                }
                else {
                    examResult.setStartTime( new Date() );
                    if ( examResult.getId() > 0 ) {
                        examDao.save( examResult );
                    }
                }
            }
        }
        if ( request.isXHR() && ( examResult == null || examResult.getQuestionResults() == null ) ) {
            showMode = ShowMode.NEED_TO_RELOAD;
            return null;
        }
        setupCurrentQuestion();

        return null;
    }

    private void setupCurrentQuestion() {
        current = examResult.getCurrentQuestion();
        if ( current.getElements() == null ) {
            current.setElements( new LinkedList<>() );
        }
        if ( current.getId() > 0 && !Hibernate.isInitialized( current.getElements() ) ) {
            // lazy init
            examDao.refresh( current );
        }
        // lazy init for Correspondence and Tree questions
        Question question = current.getQuestion();
        if ( question instanceof QuestionCorrespondence &&
                !Hibernate.isInitialized( ((QuestionCorrespondence)question).getCorrespondenceVariants() ) ) {
            examDao.refresh( question );
        }
        if ( question instanceof QuestionTree &&
                !Hibernate.isInitialized( ((QuestionTree)question).getCorrespondenceVariants() ) ) {
            examDao.refresh( question );
        }

    }

    public Object onSuccess() {
        current.checkCorrectness();
        current.setUpdated( new Date() );
        current.setAnsweredCount( current.getElements().size() );

        //save only existed result
        if ( examResult.getId() > 0 ) {
            if ( getEstimatedEndTime().before( new Date() ) ) {
                examResult.setEndTime( new Date() );
            }
            examDao.save( examResult );
        }

        if ( examResult.getQuestionResults().size() -1 == examResult.getCurrentQuestionNumber()
                || examResult.isFinished()) {
            if ( request.isXHR() ) {
                onToQuestionList();
                return null;
            }
            else {
                return TestFinal.class;
            }
        }
        return onNextQuestion();
    }

    public boolean onNextQuestion() {
        if ( examResult.isExamMode() && getEstimatedEndTime().before( new Date() ) ) {
            // check time - if testee used "Next/Prev question" button
            examResult.setEndTime( new Date() );
            examDao.save( examResult );
            showMode = ShowMode.SHOW_TABLE;
        }
        else if ( examResult.getQuestionResults().size()-1 > examResult.getCurrentQuestionNumber() ) {
            examResult.setCurrentQuestionNumber( examResult.getCurrentQuestionNumber() + 1 );
            setupCurrentQuestion();
        }
        if ( request.isXHR() ) {
            ajaxRendererSetup();
        }
        return true; // finish handling this event
    }

    public boolean onPrevQuestion() {
        if ( examResult.isExamMode() && getEstimatedEndTime().before( new Date() ) ) {
            // check time - if testee used "Next/Prev question" button
            examResult.setEndTime( new Date() );
            examDao.save( examResult );
            showMode = ShowMode.SHOW_TABLE;
        }
        else if ( examResult.getCurrentQuestionNumber() > 0 ) {
            examResult.setCurrentQuestionNumber( examResult.getCurrentQuestionNumber() - 1 );
            setupCurrentQuestion();
        }
        if ( request.isXHR() ) {
            ajaxRendererSetup();
        }
        return true;
    }

    public void onToQuestionList() {
        showMode = ShowMode.SHOW_TABLE;
        if ( request.isXHR() ) {
            ajaxResponseRenderer.addRender( questionFormZone );
        }
    }

    public void onStartTimer() {
        examResult.setStartTime( new Date() );
        if ( examResult.getId() > 0 ) {
            examDao.save( examResult );
        }
        setupCurrentQuestion();
        if ( request.isXHR() ) {
            ajaxResponseRenderer.addRender( examTimingZone );
            ajaxRendererSetup();
        }
    }

    public void onKeepThisQuestion( int questionNumber ) {
        if ( examResult.isExamMode() && getEstimatedEndTime().before( new Date() ) ) {
            // check time - if testee used "Next/Prev question" button
            examResult.setEndTime( new Date() );
            examDao.save( examResult );
            showMode = ShowMode.SHOW_TABLE;
        }
        examResult.setCurrentQuestionNumber( questionNumber );
        setupCurrentQuestion();
        if ( request.isXHR() ) {
            ajaxRendererSetup();
        }
    }

    public void onFinishTest() {
        examResult.setEndTime( new Date() );
        //save only existed result
        if ( examResult.getId() > 0 ) {
            examDao.save( examResult );
        }
        showMode = ShowMode.SHOW_TABLE;
        if ( request.isXHR() ) {
            ajaxResponseRenderer.addRender( questionFormZone );
            ajaxResponseRenderer.addRender( examTimingZone );
            if ( examResult.getExam() != null && examResult.getExam().isUseProctoring() ) {
                ajaxResponseRenderer
                        .addCallback( new JavaScriptCallback() {
                            @Override
                            public void run( JavaScriptSupport javascriptSupport ) {
                                javascriptSupport.require( "proctoring" ).invoke( "finishSession" );

                            }
                        } );  // call proctoring close session
            }
        }
    }

    private void ajaxRendererSetup() {
        ajaxResponseRenderer
                .addCallback( new JavaScriptCallback() {
                    @Override
                    public void run( JavaScriptSupport javascriptSupport ) {
                        javascriptSupport.require( "katex/contrib/auto-render" ).invoke( "renderMathInElementOfClass" ).with( "container" );

                    }
                } )
                .addRender( questionFormZone );
    }

    public boolean isNextExist() {
        return examResult.getCurrentQuestionNumber() < (examResult.getQuestionResults().size() - 1);
    }

    public boolean isPrevExist() {
        return examResult.getCurrentQuestionNumber() > 0;
    }

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

    public enum ShowMode {
        SHOW_QUESTION,
        SHOW_TABLE,
        START_TEST,
        NEED_TO_RELOAD
    }

    public Block getMainBlock() {
        if ( showMode == ShowMode.SHOW_TABLE || examResult.isFinished() ) {
            return questionListBlock;
        }
        else if (showMode == ShowMode.START_TEST ) {
            return startProctoring;
        }
        else if ( showMode == ShowMode.NEED_TO_RELOAD ) {

        }
        return questionBlock;
    }

    public String getProctoringJS() {
        String proctoringJS = propertyService.getProperty( StoredPropertyName.PROCTORING_JS_URL );
        return proctoringJS + ".js";
    }

    public boolean getUseProctoring() {
        return examResult != null && examResult.getExam() != null && examResult.getExam().isUseProctoring();
    }
}
