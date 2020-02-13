package ru.rsmu.tempoLW.components.admin.appeal;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.tempoLW.entities.*;

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
        Boolean correct = Boolean.TRUE;
        //List<AnswerVariant> correctAnswers = getCorrectAnswers();
        String result = getAnswer();
        if(result == ""){
            correct = Boolean.FALSE;
        }
        else {
            result = result.toLowerCase();
            result = result.replaceAll( "(\\d)[.](\\d)", "$1,$2" );
            for (AnswerVariant correctVariant : getCorrectAnswers()){
                String pattern = correctVariant.getRegex();
                if ( pattern != null ) {
                    // use variant text as REGEX pattern
                    if ( !result.matches( pattern ) ) {
                        correct = Boolean.FALSE;
                    }
                }
                else {
                    String[] parts = correctVariant.getText().split( "\\|" );
                    Integer correctCount = 0;
                    for ( String part : parts ) {
                        String match = part.trim().toLowerCase().replaceAll( "(\\d)[.](\\d)", "$1,$2" );
                        if ( match.length() > 0 && result.contains( match ) ) {
                            correctCount++;
                            break;
                        }
                    }
                    if (correctCount == 0){
                        correct = Boolean.FALSE;
                    }
                }
            }
        }
        return correct;
    }
}
