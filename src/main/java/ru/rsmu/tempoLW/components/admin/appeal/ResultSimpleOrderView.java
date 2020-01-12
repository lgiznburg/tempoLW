package ru.rsmu.tempoLW.components.admin.appeal;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.tempoLW.entities.*;

import java.util.ArrayList;
import java.util.List;

public class ResultSimpleOrderView {
    @Parameter(required = true)
    @Property
    private QuestionResult current;

    @Property
    private ResultSimpleOrder el;

    @Property
    private AnswerVariant answerVariant;

    @Property
    private Integer numEl;

    public List<Integer> getIntegerList(){
        List<Integer> list = new ArrayList<>();
        for (int i=0; i<getOrderedAnswers().size(); i++){
            list.add(i);
        }
        return list;
    }

    public List<ResultSimpleOrder> getOrderedAnswers(){
        List<ResultSimpleOrder> orderedAnswers = new ArrayList<>();
        for (int i =1; i<=current.getElements().size(); i++) {
            for (ResultElement e : current.getElements()) {
                ResultSimpleOrder r = (ResultSimpleOrder) e;
                //r.getAnswerVariant().getSequenceOrder()
                if(r.getEnteredOrder() == i){
                    orderedAnswers.add(r);
                    break;
                }
            }
        }
        return orderedAnswers;
    }

    public QuestionSimpleOrder getQuestion(){
        return (QuestionSimpleOrder) current.getQuestion();
    }

    public List<AnswerVariant> getOrderedCorrectAnswers(){
        List<AnswerVariant> orderedCorrectAnswers = new ArrayList<>();
        for(int i=1; i<getQuestion().getAnswerVariants().size(); i++) {
            for (AnswerVariant a : getQuestion().getAnswerVariants()) {
                if(a.getSequenceOrder() == i){
                    orderedCorrectAnswers.add(a);
                }
            }
        }
        return orderedCorrectAnswers;
    }

    public Boolean answerHasCorrectOrder(ResultSimpleOrder resultSimpleOrder){
        Boolean correct = Boolean.FALSE;
        if (resultSimpleOrder.getAnswerVariant().getSequenceOrder() == resultSimpleOrder.getEnteredOrder()){
            correct = Boolean.TRUE;
        }
        return correct;
    }

    public Boolean isCorrectAnswerExist(Integer n){
        if (n<getOrderedCorrectAnswers().size()){
            return Boolean.TRUE;
        }
        else {
            return Boolean.FALSE;
        }
    }

    public List<AnswerVariant> getMissedAnswers(){
        List<AnswerVariant> missedAnswers = new ArrayList<>();
        if (getOrderedAnswers().size()<getOrderedCorrectAnswers().size()){
            for (int i = getOrderedAnswers().size(); i<getOrderedCorrectAnswers().size(); i++){
                missedAnswers.add(getOrderedCorrectAnswers().get(i));
            }
        }
        return missedAnswers;
    }

    public void myTest(){
        QuestionSimpleOrder cr = (QuestionSimpleOrder) current.getQuestion();
        cr.getAnswerVariants();
        //current.
    }
}
