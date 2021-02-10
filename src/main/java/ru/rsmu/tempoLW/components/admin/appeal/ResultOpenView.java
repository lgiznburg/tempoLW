package ru.rsmu.tempoLW.components.admin.appeal;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.tempoLW.entities.AnswerVariant;
import ru.rsmu.tempoLW.entities.QuestionOpen;
import ru.rsmu.tempoLW.entities.QuestionResult;
import ru.rsmu.tempoLW.entities.ResultOpen;
import ru.rsmu.tempoLW.utils.CorrectnessUtils;

import java.util.List;

public class ResultOpenView {
    @Parameter(required = true)
    @Property
    private QuestionResult current;

    public String getAnswer(){
        String answer = "";
        if(!current.getElements().isEmpty()){
            ResultOpen r = (ResultOpen) current.getElements().get(0);
            answer = r.getValue();
        }
        return answer;
    }

     //for correct answers
    @Property
    private AnswerVariant correctAnswer;

    public List<AnswerVariant> getCorrectAnswers(){
        QuestionOpen questionOpen = (QuestionOpen) current.getQuestion();
        return questionOpen.getAnswerVariants();
    }

    //chek is answer correct

    public Boolean answerIsCorrect(){
        return CorrectnessUtils.countErrors( getAnswer(), getCorrectAnswers() ) == 0;
    }
}
