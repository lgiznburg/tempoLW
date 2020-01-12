package ru.rsmu.tempoLW.components.admin.appeal;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.tempoLW.entities.*;

import java.util.ArrayList;
import java.util.List;

public class ResultSimpleView {
    @Parameter(required = true)
    @Property
    private QuestionResult current;

    @Property
    private ResultSimple el;

    @Property
    private AnswerVariant answerVariant;

    public List<AnswerVariant> getCorrectAnswerVariants(){
        QuestionSimple questionSimple = (QuestionSimple) current.getQuestion();
        List<AnswerVariant> variantList = questionSimple.getAnswerVariants();
        List<AnswerVariant> correctVariantList = new ArrayList<>();

        for (AnswerVariant v : variantList){
            if(v.isCorrect()){
                correctVariantList.add(v);
            }
        }

        for (ResultElement result : current.getElements()){
            if(result.isCorrect()){
                ResultSimple r = (ResultSimple)result;
                for (AnswerVariant v : variantList){
                    if(r.getAnswerVariant() == v){
                        correctVariantList.remove(v);
                    }
                }
            }
        }
        return correctVariantList;
        //return variantList;
    }

    public void mytest(){
        //QuestionSimple questionSimple = current.getQuestion();
        QuestionSimple question = new QuestionSimple();
        question = (QuestionSimple) current.getQuestion();
        question.getAnswerVariants();
        current.getElements();
    }
}
