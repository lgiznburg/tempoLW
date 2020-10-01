package ru.rsmu.tempoLW.components.admin.appeal;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.tempoLW.entities.*;

import java.util.*;

public class ResultCorrespondenceView {
    @Parameter(required = true)
    @Property
    private QuestionResult current;

    @Property
    private CorrespondenceVariant correspondenceVariant;
    @Property
    private AnswerVariant answerVariant;

    private Map<CorrespondenceVariant,List<AnswerVariant>> missedCorrectAnswersMap = new HashMap<>();

    private Map<CorrespondenceVariant,List<AnswerVariant>> chosenAnswersMap = new HashMap<>();

    public void setupRender() {
        for ( CorrespondenceVariant variant : getQuestionList() ) {
            List<AnswerVariant> missedCorrectAnswers = missedCorrectAnswersMap.computeIfAbsent( variant, k -> new ArrayList<>() );
            List<AnswerVariant> chosenAnswers = chosenAnswersMap.computeIfAbsent( variant, k -> new ArrayList<>() );
            List<AnswerVariant> correctChooseAnswers = new ArrayList<>();
            List<ResultElement> resultElements = current.getElements();
            for(ResultElement element : resultElements){
                ResultCorrespondence correspondenceEl = (ResultCorrespondence) element;
                if(correspondenceEl.getCorrespondenceVariant() == variant){
                    chosenAnswers.add(correspondenceEl.getAnswerVariant());
                    if (correspondenceEl.isCorrect()){
                        correctChooseAnswers.add(correspondenceEl.getAnswerVariant());
                    }
                }
            }
            if(variant.getCorrectAnswers().size() != correctChooseAnswers.size()){
                for (AnswerVariant answer : variant.getCorrectAnswers()){
                    if(!correctChooseAnswers.contains(answer)){
                        missedCorrectAnswers.add(answer);
                    }
                }
            }

        }
    }

    public List<CorrespondenceVariant> getQuestionList(){
        QuestionCorrespondence questionCorrespondence = (QuestionCorrespondence) current.getQuestion();
        return questionCorrespondence.getCorrespondenceVariants();
    }

    public List<AnswerVariant> chooseAnswersList(CorrespondenceVariant correspondenceVariant){
        return chosenAnswersMap.get( correspondenceVariant );
    }

    public List<AnswerVariant> getMissedCorrectAnswers() {
        return missedCorrectAnswersMap.get( correspondenceVariant ) != null ?
                missedCorrectAnswersMap.get( correspondenceVariant ) : Collections.emptyList();
    }

    public boolean checkCorrectAnswer( AnswerVariant answerVariant ) {
        return correspondenceVariant.getCorrectAnswers().contains( answerVariant );
    }
}
