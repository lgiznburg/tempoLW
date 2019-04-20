package ru.rsmu.tempoLW.utils;

import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;

import java.util.Collections;
import java.util.LinkedList;

/**
 * @author leonid.
 */
public class ExamBuilder {
    private QuestionDao questionDao;

    public ExamBuilder( QuestionDao questionDao ) {
        this.questionDao = questionDao;
    }

    public ExamResult buildTestVariant( TestingPlan plan ) {
        ExamResult examResult = new ExamResult();
        examResult.setQuestionResults( new LinkedList<>() );
        Collections.sort( plan.getRules() );

        int questionNum = 0;
        for ( TestingPlanRule rule : plan.getRules() ) {
            for ( Question question : questionDao.findRandomQuestions( rule ) ) {
                QuestionResult questionResult = new QuestionResult();
                questionResult.setQuestion( question );
                questionResult.setOrderNumber( questionNum++ );
                questionResult.setExamResult( examResult );
                questionResult.setScoreCost( rule.getScoreCost() );
                examResult.getQuestionResults().add( questionResult );
            }
        }
        examResult.setTitle( String.format( "%s (%s)", plan.getSubject().getTitle(), plan.getName() ) );
        examResult.setTestingPlan( plan );
        return examResult;
    }
}
