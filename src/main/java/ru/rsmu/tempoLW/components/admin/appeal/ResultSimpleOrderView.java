package ru.rsmu.tempoLW.components.admin.appeal;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.tempoLW.entities.*;

import java.util.*;
import java.util.stream.Collectors;

public class ResultSimpleOrderView {
    @Parameter(required = true)
    @Property
    private QuestionResult current;

    @Property
    private Map<Integer, List<ResultSimpleOrder>> givenAnswersMap;

    @Property
    private List<AnswerVariant> orderedCorrectAnswers;

    @Property
    private ResultSimpleOrder el;

    @Property
    private AnswerVariant answerVariant;

    @Property
    private Integer numEl;

    public void setupRender() {
        // 1 - select only correct answers in correct order
        List<AnswerVariant> orderedCorrectAnswers = new ArrayList<>( getQuestion().getAnswerVariants() );
        orderedCorrectAnswers.sort( new Comparator<AnswerVariant>() {
            @Override
            public int compare( AnswerVariant a1, AnswerVariant a2 ) {
                return a1.getSequenceOrder() - a2.getSequenceOrder();
            }
        } );
        this.orderedCorrectAnswers = orderedCorrectAnswers.stream().filter( AnswerVariant::isCorrect ).collect( Collectors.toList());

        // 2 - create a map of entered answers in correct order
        givenAnswersMap = new HashMap<>();
        // 2.1 - find number of elements. it's number of correct answers or max entered order, which is greater
        int maxEnteredOrder = current.getElements().stream()
                .mapToInt( el -> ((ResultSimpleOrder)el).getEnteredOrder() )
                .max().orElse( 0 );
        int elementCount = Math.max( maxEnteredOrder, this.orderedCorrectAnswers.size() );
        for ( int i = 0; i < elementCount; i++ ) {
            List<ResultSimpleOrder> givenAnswer = new ArrayList<>();
            givenAnswersMap.put( i, givenAnswer );
            // each element in the map is a list of entered answers with given index
            int finalI = i + 1;  // index to order number
            current.getElements().forEach( a ->
                    {
                        if (((ResultSimpleOrder)a).getEnteredOrder() == finalI) {
                            givenAnswer.add( (ResultSimpleOrder)a );
                        }
                    }
            );
        }
    }

    public int getViewableNumber() {
        return numEl + 1;
    }

    public QuestionSimpleOrder getQuestion(){
        return (QuestionSimpleOrder) current.getQuestion();
    }

    public Boolean getCorrectAnswerExist(){
        boolean correctAnswerPresent = givenAnswersMap.get( numEl ).stream().anyMatch( ResultElement::isCorrect );
        return !correctAnswerPresent && numEl < orderedCorrectAnswers.size();
    }

    public Boolean getAnswerExist() {
        return givenAnswersMap.get( numEl ) != null && givenAnswersMap.get( numEl ).size() > 0;
    }

}
