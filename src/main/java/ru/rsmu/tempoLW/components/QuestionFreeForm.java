package ru.rsmu.tempoLW.components;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.upload.components.Upload;
import org.apache.tapestry5.upload.services.UploadedFile;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.QuestionResult;
import ru.rsmu.tempoLW.entities.ResultAttachedFile;
import ru.rsmu.tempoLW.entities.ResultFree;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author leonid.
 *
 *  This component will trigger the following events on its container :
 *  {@link QuestionFreeForm#KEEP_THIS_QUESTION}(Int questionNumber)
 *  This means the question is not completed, next answer should be given
 */
// @Events is applied to a component solely to document what events it may
// trigger. It is not checked at runtime.
@Events( {QuestionFreeForm.KEEP_THIS_QUESTION} )
public class QuestionFreeForm {
    public static final String KEEP_THIS_QUESTION = "keepThisQuestion";

    @Property
    private QuestionResult questionResult;

    /**
     * Current exam - stored in the session, used to extract current question from
     */
    @SessionState
    private ExamResult examResult;

    @Property
    private ResultFree resultFree;

    /**
     * for upload scanned answers
     */
    @Property
    private UploadedFile answerFile;

    @Property
    private ResultAttachedFile attachedFile;

    private boolean stayHere = false;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private Request request;

    @Inject
    private ComponentResources componentResources;

    @Inject
    private Messages messages;

    @InjectComponent("questionFreeForm")
    private Form freeForm;

    @InjectComponent("answerFile")
    private Upload fileUploadField;

    public void onPrepareForRender() {
        prepare();
    }

    public void onPrepareForSubmit() {
        if ( isSessionLost() ) {
            questionResult = new QuestionResult();
            return;
        }
        prepare();
    }

    private void prepare() {
        questionResult = examResult.getCurrentQuestion();

        questionDao.refresh( questionResult );
        if ( questionResult.getElements() != null && questionResult.getElements().size() > 0 ) {
            resultFree = (ResultFree) questionResult.getElements().get( 0 );
        }
        else {
            resultFree = new ResultFree();
            resultFree.setQuestionResult( questionResult );
        }
    }

    public void onSelectedFromAttachFile() {
        stayHere = true;
    }

    public void onValidate() {
        if ( answerFile != null && !answerFile.getContentType().matches( "(image.*)|(application/pdf)" ) ) {
            freeForm.recordError( fileUploadField, messages.get( "incorrect-file-type" ) );
        }
    }

    public boolean onSuccess() {
        if ( isSessionLost() ) return false;
        // find correct current question. NB: should it be checked for type?
        questionResult = examResult.getCurrentQuestion();

        if ( questionResult.getElements() == null ) {
            questionResult.setElements( new ArrayList<>() );
        }

        try {
            if ( answerFile != null && answerFile.getContentType().matches( "(image.*)|(application/pdf)" ) ) {
                if ( resultFree.getFiles() == null ) {
                    resultFree.setFiles( new ArrayList<>() );
                }
                ResultAttachedFile file = new ResultAttachedFile();
                file.setContentType( answerFile.getContentType() );
                file.setSourceName( answerFile.getFileName() );
                file.setContent( IOUtils.toByteArray( answerFile.getStream() ) );
                file.setResultElement( resultFree );
                file.setSize( file.getContent().length );
                resultFree.getFiles().add( file );
                questionDao.save( resultFree );
                questionDao.save( file );
            }
        } catch (IOException e) {
            //
        }
        if ( resultFree.getFiles() != null && resultFree.getFiles().size() > 0 ) {
            questionDao.save( resultFree );
        }
        else if ( resultFree.getId() != 0 ) {
            questionResult.setAnsweredCount( 0 );
            questionDao.save( questionResult );
        }

        if ( stayHere ) {
            componentResources.triggerEvent( KEEP_THIS_QUESTION, new Object[] {examResult.getCurrentQuestionNumber()}, null );
            return true;
            // we do not pass control to up level.
        }
        return false;
    }

    public boolean onDeleteAnswer( ResultAttachedFile file ) {
        questionDao.delete( file );
        componentResources.triggerEvent( KEEP_THIS_QUESTION, new Object[] {examResult.getCurrentQuestionNumber()}, null );
        return true;
    }

    /**
     * If session expire examResult becomes empty. So we need to show friendly message instead of NPE exception
     * @return true if everything is OK, false if examResult is empty
     */
    private boolean isSessionLost() {
        return request.isXHR() && (examResult == null || examResult.getQuestionResults() == null);
    }

    public boolean isExamMode() {
        return examResult.isExamMode();
    }
}
