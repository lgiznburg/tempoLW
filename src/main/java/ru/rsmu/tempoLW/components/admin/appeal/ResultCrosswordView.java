package ru.rsmu.tempoLW.components.admin.appeal;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.utils.CorrectnessUtils;

import java.util.List;

/**
 * @author leonid.
 */
public class ResultCrosswordView {
    @Parameter(required = true)
    @Property
    private QuestionResult current;

    @Property
    private CorrespondenceVariant correspondenceVariant;
    @Property
    private AnswerVariant answerVariant;


    public List<CorrespondenceVariant> getQuestionList(){
        QuestionCrossword questionTree = (QuestionCrossword) current.getQuestion();
        return questionTree.getCorrespondenceVariants();
    }

    public String getCurrentAnswer() {
        ResultTreeOpen currentResult = (ResultTreeOpen)current.getElements().stream().filter( e ->
                ((ResultTreeOpen)e).getCorrespondenceVariant().getId() == correspondenceVariant.getId() )
                .findFirst().orElse( null );
        return currentResult != null ? currentResult.getValue() : "";
    }

    public String getCurrentVariantText() {
        return correspondenceVariant.getText().split( "%%" )[0];
    }

    /**
     * Match answer against answer variants for current correspondence
     * @return true if answer correct
     */
    public Boolean answerIsCorrect() {
        return CorrectnessUtils.countErrors( getCurrentAnswer(), correspondenceVariant.getCorrectAnswers() ) == 0;
    }

}
