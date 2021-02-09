package ru.rsmu.tempoLW.components;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ValueEncoderSource;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.pages.QuestionImage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author leonid.
 *
 *  This component will trigger the following events on its container :
 *  {@link QuestionTreeForm#KEEP_THIS_QUESTION}(Int questionNumber)
 *  This means the question is not completed, next answer should be given
 */
// @Events is applied to a component solely to document what events it may
// trigger. It is not checked at runtime.
@Events( {QuestionTreeForm.KEEP_THIS_QUESTION} )
public class QuestionTreeOpenForm {
    @Property
    private QuestionResult questionResult;

    /**
     * Current exam - stored in the session, used to extract current question from
     */
    @SessionState
    private ExamResult examResult;

    @Property
    @Persist(PersistenceConstants.SESSION)
    private int internalStep;

    @Property
    @Persist(PersistenceConstants.SESSION)
    private long questionId;

    //@Property
    //private SelectModel answerModel;

    //@Property
    //private List<AnswerVariant> selectedAnswers;

    @Property
    private ResultTreeOpen resultOpen;

    @Property
    private CorrespondenceVariant currentVariant;

    @Property
    private String previousAnswerText;

    @Inject
    private SelectModelFactory modelFactory;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private LinkSource linkSource;

    @Inject
    private ComponentResources componentResources;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    @Inject
    private Request request;

    public void setupRender() {
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

        QuestionTreeOpen question = (QuestionTreeOpen) questionResult.getQuestion();
        // Lazy init
        questionDao.refresh( question );

        if ( questionId != question.getId() ) {
            questionId = question.getId();
            internalStep = 0;
        }

        //List<CorrespondenceVariant> variants = questionDao.findCorrespondenceVariants( question );
        // get current step - currentVariant
        if ( internalStep < question.getCorrespondenceVariants().size() ) {
            currentVariant = question.getCorrespondenceVariants().get( internalStep );
        }
        if ( questionResult.getElements() == null ) {
            questionResult.setElements( new ArrayList<>() );
        }
        resultOpen = (ResultTreeOpen) questionResult.getElements().stream().filter( e ->
                ((ResultTreeOpen)e).getCorrespondenceVariant().getId() == currentVariant.getId()
        ).findFirst().orElse( null );

        if ( resultOpen == null ) {
            resultOpen = new ResultTreeOpen();
            resultOpen.setCorrespondenceVariant( currentVariant );
            resultOpen.setQuestionResult( questionResult );
            //questionResult.getElements().add( resultOpen );
        }

    }

    public boolean onSuccess() {
        if ( isSessionLost() ) return false;
        // create elements if not exist
        if ( questionResult.getElements() == null ) {
            questionResult.setElements( new LinkedList<>() );
        }
        //  question result
        if ( StringUtils.isBlank( resultOpen.getValue() ) ) {
            questionResult.getElements().remove( resultOpen );
            if ( resultOpen.getId() > 0 ) {
                questionDao.delete( resultOpen );
            }
        }
        else if ( !questionResult.getElements().contains( resultOpen ) ) {
            questionResult.getElements().add( resultOpen );
        }

        // submit event bubbles up to page level, save result and log there

        // go to next step
        internalStep++;
        if ( internalStep >= ((QuestionTreeOpen) questionResult.getQuestion()).getCorrespondenceVariants().size() ) {
            internalStep = 0;
            // this tree is finished
            return false;
        }
        // this tree is unfinished, trigger another event to keep question number unchanged
        if ( questionResult.getId() > 0 ) {
            questionDao.save( questionResult );
        }
        componentResources.triggerEvent( QuestionTreeForm.KEEP_THIS_QUESTION, new Object[] {examResult.getCurrentQuestionNumber()}, null );
        return true;
        // we do not pass control to up level.
    }

    public ValueEncoder<AnswerVariant> getAnswerEncoder() {
        return valueEncoderSource.getValueEncoder( AnswerVariant.class );
    }

    public List<String> getPreviousAnswers() {
        List<String> answers = new ArrayList<>();
        if ( questionResult.getElements().size() > 0 ) {
            for ( CorrespondenceVariant previousVariant : ((QuestionTreeOpen)questionResult.getQuestion()).getCorrespondenceVariants() ) {
                ResultTreeOpen currentAnswer = (ResultTreeOpen) questionResult.getElements().stream().filter( e ->
                    ((ResultTreeOpen)e).getCorrespondenceVariant().getId() == previousVariant.getId()
                 ).findFirst().orElse( null );

                String text = previousVariant.getText();
                String replacement; //no answer
                if ( currentAnswer != null ) {
                    String spanClass = "alert-warning";
                    if ( previousVariant == currentVariant ) {
                        spanClass = "alert-info";
                    }
                    replacement = String.format( "<span class=\"alert %s\" style=\"padding:1px 10px;\"> %s </span>", spanClass,  StringUtils.join( currentAnswer.getValue(), ", " ) );
                }
                else {
                    replacement = "<span class=\"alert alert-danger\" style=\"padding:1px 10px;\">...</span>"; //no answer
                }

                if ( text.contains( "…" ) || text.contains( "..." ) ) {
                    answers.add( text.replaceAll( "(…)|(\\.\\.\\.)", replacement ) );
                }
                else {
                    answers.add( text + " : " + replacement );
                }
                if ( previousVariant == currentVariant ) {
                    break;
                }
            }
        }
        return answers;
    }

    public boolean isPreviousStepPresent() {
        return internalStep > 0;
    }

    public boolean isNextStepPresent() {
        return ((QuestionTreeOpen) questionResult.getQuestion()).getCorrespondenceVariants().size() - 1 > internalStep;
    }

    public boolean onOneStepBack() {
        if ( internalStep > 0 ) internalStep--;
        // just trigger another event to keep question number unchanged
        componentResources.triggerEvent( QuestionTreeForm.KEEP_THIS_QUESTION, new Object[] {examResult.getCurrentQuestionNumber()}, null );
        return true; // don't pass control to up level
    }

    public String getCorrespondeceImageLink() {
        if ( currentVariant.getImage() != null ) {
            return linkSource.createPageRenderLink( QuestionImage.class.getSimpleName(), false, currentVariant.getImage().getId() ).toURI();
        }
        return "";
    }

    /**
     * If session expire examResult becomes empty. So we need to show friendly message instead of NPE exception
     * @return true if everything is OK, false if examResult is empty
     */
    private boolean isSessionLost() {
        return request.isXHR() && (examResult == null || examResult.getQuestionResults() == null);
    }
}
