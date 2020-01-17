package ru.rsmu.tempoLW.components.admin.appeal;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.tempoLW.entities.*;

import java.util.ArrayList;
import java.util.List;

public class ResultTreeView {
    @Parameter(required = true)
    @Property
    private QuestionResult current;

    @Property
    private CorrespondenceVariant correspondenceVariant;
    @Property
    private AnswerVariant answerVariant;
    @Property
    private List<AnswerVariant> missedCorrectAnswers = new ArrayList<>();


    public List<CorrespondenceVariant> getQuestionList(){
        QuestionTree questionTree = (QuestionTree) current.getQuestion();
        return questionTree.getCorrespondenceVariants();
    }

    public List<AnswerVariant> chooseAnswersList(CorrespondenceVariant correspondenceVariant){
        missedCorrectAnswers = new ArrayList<>();
        List<AnswerVariant> chooseAnswers = new ArrayList<>();
        List<AnswerVariant> correctChooseAnswers = new ArrayList<>();
        List<ResultElement> resultElements = current.getElements();
        for(ResultElement element : resultElements){
            ResultTree resultTree = (ResultTree) element;
            if(resultTree.getCorrespondenceVariant() == correspondenceVariant) {
                chooseAnswers.add(resultTree.getAnswerVariant());
                if(resultTree.isCorrect()){
                    correctChooseAnswers.add(resultTree.getAnswerVariant());
                }
            }
        }
        for (AnswerVariant variant : correspondenceVariant.getCorrectAnswers()) {
            if (!correctChooseAnswers.contains(variant)) {
                if(variant.isCorrect()) {
                    missedCorrectAnswers.add(variant);
                }
            }
        }
        return chooseAnswers;
    }

}
