package ru.rsmu.tempoLW.components.admin.appeal;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.tempoLW.entities.*;

import java.util.ArrayList;
import java.util.List;

public class ResultCorrespondenceView {
    @Parameter(required = true)
    @Property
    private QuestionResult current;

    @Property
    private CorrespondenceVariant correspondenceVariant;
    @Property
    private AnswerVariant answerVariant;

    public List<CorrespondenceVariant> getQuestionList(){
        QuestionCorrespondence questionCorrespondence = (QuestionCorrespondence) current.getQuestion();
        return questionCorrespondence.getCorrespondenceVariants();
    }

    public List<AnswerVariant> chooseAnswersList(CorrespondenceVariant correspondenceVariant){
        missedCorrectAnswers = new ArrayList<>();
        List<AnswerVariant> chooseAnswers = new ArrayList<>();
        List<AnswerVariant> correctChooseAnswers = new ArrayList<>();
        List<ResultElement> resultElements = current.getElements();
        for(ResultElement element : resultElements){
            ResultCorrespondence correspondenceEl = (ResultCorrespondence) element;
            if(correspondenceEl.getCorrespondenceVariant() == correspondenceVariant){
                chooseAnswers.add(correspondenceEl.getAnswerVariant());
                if (correspondenceEl.isCorrect()){
                    correctChooseAnswers.add(correspondenceEl.getAnswerVariant());
                }
            }
        }
        if(correspondenceVariant.getCorrectAnswers().size() != correctChooseAnswers.size()){
            for (AnswerVariant variant : correspondenceVariant.getCorrectAnswers()){
                if(!correctChooseAnswers.contains(variant)){
                    missedCorrectAnswers.add(variant);
                }
            }
        }
        return chooseAnswers;
    }

    @Property
    private List<AnswerVariant> missedCorrectAnswers = new ArrayList<>();
}
