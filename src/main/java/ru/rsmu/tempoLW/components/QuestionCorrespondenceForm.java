package ru.rsmu.tempoLW.components;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
public class QuestionCorrespondenceForm {
    @Parameter(required = true)
    @Property
    private QuestionResult questionResult;

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

    public void setupRender() {
        prepare();
    }

    public void onPrepareForSubmit() {
        prepare();
    }


    private void prepare() {
        rows = new LinkedList<>();
        QuestionCorrespondence question = (QuestionCorrespondence) questionResult.getQuestion();
        // Lazy init
        questionDao.refresh( question );
        Collections.shuffle( question.getAnswerVariants() );
        answerModel = modelFactory.create( question.getAnswerVariants(), "text" );

        Map<Long,CorrespondenceRow> rowMap = new HashMap<>();
        for ( CorrespondenceVariant correspondenceVariant : question.getCorrespondenceVariants() ) {
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
                    // todo questionDao.delete(element)
                    elementIt.remove();
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
    }

    public ValueEncoder<AnswerVariant> getAnswerEncoder() {
        return new ValueEncoder<AnswerVariant>() {
            @Override
            public String toClient( AnswerVariant value ) {
                return String.valueOf( value.getId() );
            }

            @Override
            public AnswerVariant toValue( String clientValue ) {
                long id = Long.parseLong( clientValue );
                for ( AnswerVariant variant : ( (QuestionCorrespondence) questionResult.getQuestion() ).getAnswerVariants() ) {
                    if ( variant.getId() == id ) {
                        return variant;
                    }
                }
                return null;
            }
        };
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

}
