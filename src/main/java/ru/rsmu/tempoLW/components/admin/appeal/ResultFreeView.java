package ru.rsmu.tempoLW.components.admin.appeal;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.services.SecurityUserHelper;

import java.util.Date;

/**
 * @author leonid.
 */
public class ResultFreeView {

    @Parameter(required = true)
    @Property
    private QuestionResult current;

    @Property
    private ResultAttachedFile file;

    @Property
    private ResultEvaluation evaluation;

    @Property
    @Persist(PersistenceConstants.FLASH)
    private String evaluationScore;

    @Property
    private ResultEvaluation eval;    // for the loop

    @Inject
    private ExamDao examDao;

    @Inject
    private SecurityUserHelper userHelper;

    @InjectComponent
    private Form resultEvaluationForm;

    @InjectComponent("evaluationScore")
    private TextField evaluationScoreField;

    @Inject
    private Messages messages;

    public void onPrepareForRender() {
        prepare();
    }

    public void onPrepareForSubmit() {
        prepare();
    }

    private void prepare() {
        evaluation = new ResultEvaluation();
        evaluation.setResultElement( getResultFree() );
    }

    public ResultFree getResultFree() {
        return current.getElements() != null && current.getElements().size() > 0 ? (ResultFree) current.getElements().get( 0 ) : null;
    }

    public void onValidateFromResultEvaluationForm() {
        if ( StringUtils.isNotBlank( evaluationScore ) ) {
            if ( !evaluationScore.matches( "\\d*" ) ) {
                resultEvaluationForm.recordError( evaluationScoreField, messages.get( "should-be-number" ) );
                return;
            }
            int score = Integer.parseInt( evaluationScore );
            if ( score > current.getQuestion().getQuestionInfo().getMaxScore() ) {
                resultEvaluationForm.recordError( evaluationScoreField, messages.format( "should-be-less", current.getQuestion().getQuestionInfo().getMaxScore() ) );
                return;
            }
            evaluation.setScore( score );
        }
    }

    public void onSuccess() {
        evaluation.setUpdated( new Date() );
        evaluation.setExaminer( userHelper.getCurrentUser() );
        examDao.save( evaluation );
        current.setScore( evaluation.getScore() );
        current.setMark( evaluation.getScore() * current.getScoreCost() );

        ExamResult examResult = current.getExamResult();
        int finalMark = examResult.getQuestionResults().stream().mapToInt( QuestionResult::getMark ).sum();
        examResult.setMarkTotal( finalMark );
        examDao.save( examResult );
    }
}
