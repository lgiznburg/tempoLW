package ru.rsmu.tempoLW.components;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
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

import java.util.*;

/**
 * @author leonid.
 */
public class QuestionCorrespondenceForm {
    @Property
    private QuestionResult questionResult;

    /**
     * Current exam - stored in the session, used to extract current question from
     */
    @SessionState
    private ExamResult examResult;

    @Property
    private List<CorrespondenceRow> rows;

    @Property
    private CorrespondenceRow row;

    @Property
    private SelectModel answerModel;

    @Inject
    private SelectModelFactory modelFactory;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private LinkSource linkSource;

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

        rows = new LinkedList<>();
        QuestionCorrespondence question = (QuestionCorrespondence) questionResult.getQuestion();
        // Lazy init
        questionDao.refresh( question );
        Collections.shuffle( question.getAnswerVariants() );
        answerModel = modelFactory.create( question.getAnswerVariants(), "text" );

//        List<CorrespondenceVariant> variants = questionDao.findCorrespondenceVariants( question );
        Map<Long,CorrespondenceRow> rowMap = new LinkedHashMap<>();
        for ( CorrespondenceVariant correspondenceVariant : /*variants*/ question.getCorrespondenceVariants() ) {
            CorrespondenceRow row = new CorrespondenceRow( correspondenceVariant );
            rowMap.put( correspondenceVariant.getId(), row );
        }

        if ( questionResult.getElements() != null && questionResult.getElements().size() > 0 ) {
            for ( ResultElement element : questionResult.getElements() ) {
                ResultCorrespondence correspondence = (ResultCorrespondence) element;
                CorrespondenceRow row = rowMap.get( correspondence.getCorrespondenceVariant().getId() );
                if ( row != null ) {
                    row.getSelectedAnswers().add( correspondence.getAnswerVariant() );
                }
            }
        }

        rows.addAll( rowMap.values() );
        //Collections.shuffle( rows );
    }

    public void onSuccess() {
        if ( isSessionLost() ) return;
        // find correct current question. NB: should it be checked for type?
        questionResult = examResult.getCurrentQuestion();

        if ( questionResult.getElements() == null ) {
            questionResult.setElements( new LinkedList<>() );
        }
        Map<Long,CorrespondenceRow> rowMap = new HashMap<>();
        for ( CorrespondenceRow row : rows ) {
            rowMap.put( row.getCorrespondenceVariant().getId(), row );
        }

        for ( Iterator<ResultElement> elementIt = questionResult.getElements().iterator(); elementIt.hasNext(); ) {
            ResultCorrespondence element = (ResultCorrespondence) elementIt.next();
            CorrespondenceRow checkRow = rowMap.get( element.getCorrespondenceVariant().getId() );
            if ( checkRow != null ) { // just in case
                if ( !checkRow.getSelectedAnswers().contains( element.getAnswerVariant() ) ) {
                    elementIt.remove();
                    if ( element.getId() != 0 ) {
                        // delete element from DB if exists.
                        questionDao.delete( element );
                    }
                }
            }
        }
        for( CorrespondenceRow row : rows ) {
            for ( AnswerVariant selected : row.getSelectedAnswers() ) {
                boolean found = false;
                for ( ResultElement element : questionResult.getElements() ) {
                    ResultCorrespondence elementC = (ResultCorrespondence) element;
                    if ( elementC.getCorrespondenceVariant().getId() == row.getCorrespondenceVariant().getId()
                            && elementC.getAnswerVariant().getId() == selected.getId() ) {
                        found = true;
                        break;
                    }
                }
                if ( !found ) {
                    ResultCorrespondence resultFinal = new ResultCorrespondence();
                    resultFinal.setQuestionResult( questionResult );
                    resultFinal.setCorrespondenceVariant( row.getCorrespondenceVariant() );
                    resultFinal.setAnswerVariant( selected );
                    questionResult.getElements().add( resultFinal );
                }
            }
        }
    }



    public class CorrespondenceRow {
        private List<AnswerVariant> selectedAnswers;

        private CorrespondenceVariant correspondenceVariant;

        public CorrespondenceRow( CorrespondenceVariant correspondenceVariant ) {
            this.correspondenceVariant = correspondenceVariant;
            selectedAnswers = new LinkedList<>();
        }

        public List<AnswerVariant> getSelectedAnswers() {
            return selectedAnswers;
        }

        public void setSelectedAnswers( List<AnswerVariant> selectedAnswers ) {
            this.selectedAnswers = selectedAnswers;
        }

        public CorrespondenceVariant getCorrespondenceVariant() {
            return correspondenceVariant;
        }

        public void setCorrespondenceVariant( CorrespondenceVariant correspondenceVariant ) {
            this.correspondenceVariant = correspondenceVariant;
        }

        public String getImageLink() {
            if ( correspondenceVariant.getImage() != null ) {
                return linkSource.createPageRenderLink( QuestionImage.class.getSimpleName(), false, correspondenceVariant.getImage().getId() ).toURI();
            }
            return "";
        }
    }

    public ValueEncoder<AnswerVariant> getAnswerEncoder() {
        return valueEncoderSource.getValueEncoder( AnswerVariant.class );
    }

    public ValueEncoder<CorrespondenceRow> getRowEncoder() {
        return new ValueEncoder<CorrespondenceRow>() {
            @Override
            public String toClient( CorrespondenceRow value ) {
                return String.valueOf( value.correspondenceVariant.getId() );
            }

            @Override
            public CorrespondenceRow toValue( String clientValue ) {
                long id = Long.parseLong( clientValue );
                for ( CorrespondenceRow row : rows ) {
                    if ( row.getCorrespondenceVariant().getId() == id ) {
                        return row;
                    }
                }
                return null;
            }
        };
    }

    /**
     * If session expire examResult becomes empty. So we need to show friendly message instead of NPE exception
     * @return true if everything is OK, false if examResult is empty
     */
    private boolean isSessionLost() {
        return request.isXHR() && (examResult == null || examResult.getQuestionResults() == null);
    }
}
