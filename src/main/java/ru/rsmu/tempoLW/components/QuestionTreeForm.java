package ru.rsmu.tempoLW.components;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ValueEncoderSource;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.pages.QuestionImage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author leonid.
 *
 *  This component will trigger the following events on its container :
 *  {@link QuestionTreeForm#KEEP_THIS_QUESTION}(Int questionNumber)
 */
// @Events is applied to a component solely to document what events it may
// trigger. It is not checked at runtime.
@Events( {QuestionTreeForm.KEEP_THIS_QUESTION} )
public class QuestionTreeForm {
    public static final String KEEP_THIS_QUESTION = "keepThisQuestion";

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

    @Property
    private SelectModel answerModel;

    @Property
    private List<AnswerVariant> selectedAnswers;

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

    public void setupRender() {
        prepare();
    }

    public void onPrepareForSubmit() {
        prepare();
    }


    private void prepare() {
        questionResult = examResult.getCurrentQuestion();

        QuestionTree question = (QuestionTree) questionResult.getQuestion();
        // Lazy init
        questionDao.refresh( question );

        if ( questionId != question.getId() ) {
            questionId = question.getId();
            internalStep = 0;
        }

        // get current step - currentVariant
        if ( internalStep < question.getCorrespondenceVariants().size() ) {
            currentVariant = question.getCorrespondenceVariants().get( internalStep );
            Collections.shuffle( currentVariant.getCorrectAnswers() );
            answerModel = modelFactory.create( currentVariant.getCorrectAnswers(), "text" );
        }

        //prepare answers for current step if any
        selectedAnswers = new LinkedList<>();
        if ( questionResult.getElements() != null && questionResult.getElements().size() > 0 ) {
            for ( ResultElement element : questionResult.getElements() ) {
                ResultTree elementTree = (ResultTree) element;
                if ( elementTree.getCorrespondenceVariant().getId() == currentVariant.getId() ) {
                    selectedAnswers.add( elementTree.getAnswerVariant() );
                }
            }
        }

    }

    public boolean onSuccess() {
        // create elements if not exist
        if ( questionResult.getElements() == null ) {
            questionResult.setElements( new LinkedList<>() );
        }
        // collect existed results for question result
        List<AnswerVariant> existedAnswers = new LinkedList<>();
        existedAnswers.addAll( questionResult.getElements().stream().map( element -> ((ResultTree) element).getAnswerVariant() ).collect( Collectors.toList() )  );
        //check if existed result is still checked, and remove it if it's needed
        for ( Iterator<ResultElement> elementIt = questionResult.getElements().iterator(); elementIt.hasNext(); ) {
            ResultElement element = elementIt.next();
            if ( ((ResultTree)element).getCorrespondenceVariant() == currentVariant && !selectedAnswers.contains( ((ResultTree)element).getAnswerVariant() ) ) {
                if ( element.getId() != 0 ) {
                    // delete element from DB if exists.
                    questionDao.delete( element );
                }
                elementIt.remove();
            }
        }
        // add checked answer to result list
        for ( AnswerVariant variant : selectedAnswers ) {
            if ( !existedAnswers.contains( variant ) ) {
                ResultTree resultTree = new ResultTree();
                resultTree.setQuestionResult( questionResult );
                resultTree.setCorrespondenceVariant( currentVariant );
                resultTree.setAnswerVariant( variant );
                questionResult.getElements().add( resultTree );
            }
        }

        // submit event bubbles up to page level, save result and log there

        // go to next step
        internalStep++;
        if ( internalStep >= ((QuestionTree) questionResult.getQuestion()).getCorrespondenceVariants().size() ) {
            internalStep = 0;
            // this tree is finished
            return false;
        }
        // this tree is unfinished, trigger another event to keep question number unchanged
        componentResources.triggerEvent( KEEP_THIS_QUESTION, new Object[] {examResult.getCurrentQuestionNumber()}, null );
        return true;
        // we do not pass control to up level.
    }

    public ValueEncoder<AnswerVariant> getAnswerEncoder() {
        return valueEncoderSource.getValueEncoder( AnswerVariant.class );
        /*return new ValueEncoder<AnswerVariant>() {
            @Override
            public String toClient( AnswerVariant value ) {
                return String.valueOf( value.getId() );
            }

            @Override
            public AnswerVariant toValue( String clientValue ) {
                long id = Long.parseLong( clientValue );
                for ( AnswerVariant variant : currentVariant.getCorrectAnswers() ) {
                    if ( variant.getId() == id ) {
                        return variant;
                    }
                }
                return null;
            }
        };*/
    }

    public List<String> getPreviousAnswers() {
        int existedAnswers = questionResult.getElements().size();
        List<String> answers = new ArrayList<>();
        for ( CorrespondenceVariant previousVariant : ((QuestionTree)questionResult.getQuestion()).getCorrespondenceVariants() ) {
            if ( existedAnswers <= 0 ) {
                break;  // do not go if no more answers present
            }
            List<String> currentAnswers = new ArrayList<>();
            for ( ResultElement resultElement : questionResult.getElements() ) {
                ResultTree resultTree = (ResultTree) resultElement;
                if ( resultTree.getCorrespondenceVariant() == previousVariant && resultTree.getAnswerVariant() != null ) {
                    currentAnswers.add( resultTree.getAnswerVariant().getText() );
                    existedAnswers--;
                }
            }
            String text = previousVariant.getText();
            String replacement = "<span class=\"alert alert-danger\" style=\"padding:1px 10px;\">...</span>"; //no answer
            if ( currentAnswers.size() > 0 ) {
                String spanClass = "alert-warning";
                if ( previousVariant == currentVariant ) {
                    spanClass = "alert-info";
                }
                replacement = String.format( "<span class=\"alert %s\" style=\"padding:1px 10px;\"> %s </span>", spanClass,  StringUtils.join( currentAnswers, ", " ) );
            }

            if ( text.contains( "…" ) || text.contains( "..." ) ) {
                answers.add( text.replaceAll( "(…)|(\\.\\.\\.)", replacement ) );
            }
            else {
                answers.add( text + " : " + replacement );
            }
        }
        return answers;
    }

    public boolean isPreviousStepPresent() {
        return internalStep > 0;
    }

    public boolean isNextStepPresent() {
        return ((QuestionTree) questionResult.getQuestion()).getCorrespondenceVariants().size() - 1 > internalStep;
    }

    public boolean onOneStepBack() {
        if ( internalStep > 0 ) internalStep--;
        // just trigger another event to keep question number unchanged
        componentResources.triggerEvent( KEEP_THIS_QUESTION, new Object[] {examResult.getCurrentQuestionNumber()}, null );
        return true; // do't pass control to up level
    }

    public String getCorrespondeceImageLink() {
        if ( currentVariant.getImage() != null ) {
            return linkSource.createPageRenderLink( QuestionImage.class.getSimpleName(), false, currentVariant.getImage().getId() ).toURI();
        }
        return "";
    }
}
