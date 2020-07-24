package ru.rsmu.tempoLW.pages;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.HttpError;
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

import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
    private Block questionBlock, questionListBlock, startProctoring, needToReload;

    /**
     * Request for handling AJAX async requests
     */
    @Inject
    private Request request;

    @Inject
    @Symbol(SymbolConstants.CONTEXT_PATH)
    private String contextPath;

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
            javaScriptSupport.require( "proctoring" ).invoke( "startSession" ).with( proctoringServer, getToken() );
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
        } else {
            if ( isSessionLost() )  {
                showMode = ShowMode.NEED_TO_RELOAD;
                return null;
            }
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
        if ( checkSessionDisruption() ) return true;

        current.checkCorrectness();
        current.setUpdated( new Date() );
        current.setAnsweredCount( current.getElements().size() );

        //save only existed result
        if ( examResult.getId() > 0 ) {
            if ( getEstimatedEndTime().before( new Date() ) ) {
                //examResult.setEndTime( new Date() );
                onFinishTest();
                return true;
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
        if ( checkSessionDisruption() ) return true;

        if ( examResult.isExamMode() && getEstimatedEndTime().before( new Date() ) ) {
            // check time - if testee used "Next/Prev question" button
            onFinishTest();
            return true;
/*
            examResult.setEndTime( new Date() );
            examDao.save( examResult );
            showMode = ShowMode.SHOW_TABLE;
*/
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
        if ( checkSessionDisruption() ) return true;

        if ( examResult.isExamMode() && getEstimatedEndTime().before( new Date() ) ) {
            // check time - if testee used "Next/Prev question" button
            onFinishTest();
            return true;
/*
            examResult.setEndTime( new Date() );
            examDao.save( examResult );
            showMode = ShowMode.SHOW_TABLE;
*/
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
        if ( checkSessionDisruption() ) return;

        if ( examResult.isExamMode() && getEstimatedEndTime().before( new Date() ) ) {
            // check time - if testee used "Next/Prev question" button
            onFinishTest();
            return;
        }
        showMode = ShowMode.SHOW_TABLE;
        if ( request.isXHR() ) {
            ajaxResponseRenderer.addRender( questionFormZone );
        }
    }

    public void onStartTimer() {
        if ( checkSessionDisruption() ) return;

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
        if ( checkSessionDisruption() ) return;

        if ( examResult.isExamMode() && getEstimatedEndTime().before( new Date() ) ) {
            // check time - if testee used "Next/Prev question" button
            onFinishTest();
            return;
/*
            examResult.setEndTime( new Date() );
            examDao.save( examResult );
            showMode = ShowMode.SHOW_TABLE;
*/
        }
        examResult.setCurrentQuestionNumber( questionNumber );
        setupCurrentQuestion();
        if ( request.isXHR() ) {
            ajaxRendererSetup();
        }
    }

    public void onFinishTest() {
        if ( checkSessionDisruption() ) return;

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
            ExamSchedule exam = examResult.getExam();
            if ( exam.getPeriodEndTime() != null ) {
                if ( calendar.getTime().after( exam.getPeriodEndTime() ) ) {
                    return exam.getPeriodEndTime();
                }
            }
        }
        return calendar.getTime();
    }

    /**
     * If session expire examResult becomes empty. So we need to show friendly message instead of NPE exception
     * Returns inverted status
     * @return false if everything is OK, true if examResult is empty
     */
    private boolean checkSessionDisruption() {
        if ( isSessionLost() ) {
            showMode = ShowMode.NEED_TO_RELOAD;
            ajaxResponseRenderer.addRender( questionFormZone );
            return true;
        }
        return false;
    }

    private boolean isSessionLost() {
        return request.isXHR() && ( examResult == null || examResult.getQuestionResults() == null );
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
            return needToReload;
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

    private String getToken() {
        ExamSchedule exam = examResult.getExam();
        Testee testee = examResult.getTestee();

        String sessionId = String.format( "%s-%s-%d", testee.getLogin(),
                testee.getCaseNumber(),
                exam.getId() );
        sessionId = sessionId.replaceAll( " ", "" );

        Map<String,Object> payload = new HashMap<>();

        Calendar expiration = Calendar.getInstance();
        if ( examResult.getStartTime() != null ) {
            expiration.setTime( examResult.getStartTime() ); // in case of restart
        }
        expiration.add( Calendar.HOUR, exam.getDurationHours() );
        expiration.add( Calendar.MINUTE, exam.getDurationMinutes() + 20 ); //extra 20 min
        JWTCreator.Builder jwtBuilder = JWT.create()
                .withExpiresAt( expiration.getTime() )
                .withClaim( "username", testee.getCaseNumber() )
                //.withClaim( "role", "student" )
                .withClaim( "nickname", testee.getLastName() )
                .withClaim( "id", sessionId )      // proctoring session
                .withClaim( "subject", exam.getName() + " - " + exam.getTestingPlan().getSubject().getTitle() ); // session name

        if ( propertyService.getPropertyAsInt( StoredPropertyName.PROCTORING_CALLBACK_ALLOWED ) > 0 ) {
            String schema = "https://"; // request.isSecure() ? "https://" : "http://";
            String server = request.getServerName(); //"85.142.163.82";

            if ( propertyService.getPropertyAsInt( StoredPropertyName.PROCTORING_DEBUG_ENVIRONMENT ) > 0 ) { // for debug only - dev's comp params
                schema = "http://";
                server = "85.142.163.82";
            }

            int port = request.getServerPort();
            StringBuilder callbackUri = new StringBuilder();
            callbackUri.append( schema ).append( server );
            if ( port != 0 && port != 80 ) callbackUri.append( ":" ).append( port );
            callbackUri.append( contextPath ).append( "/rest/proctoring/result" );
            jwtBuilder.withClaim( "api", callbackUri.toString() );   // callback address
        }

        String secretString = propertyService.getProperty( StoredPropertyName.PROCTORING_SECRET_KEY );
        Algorithm algorithmHS = Algorithm.HMAC256( secretString );
        String token = "";
        try {
            token = jwtBuilder
                    .sign( algorithmHS );
        } catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.
            //return new HttpError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can't create token or convert it into JSON" );
        }
        return token;
    }

}

