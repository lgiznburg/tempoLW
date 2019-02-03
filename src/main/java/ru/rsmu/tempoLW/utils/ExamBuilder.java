package ru.rsmu.tempoLW.utils;

import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;

import java.util.LinkedList;

/**
 * @author leonid.
 */
public class ExamBuilder {
    private QuestionDao questionDao;

    public ExamBuilder( QuestionDao questionDao ) {
        this.questionDao = questionDao;
    }

    public TestResult buildTestVariant( TestingPlan plan ) {
        TestResult testResult = new TestResult();
        testResult.setQuestionResults( new LinkedList<>() );

        int questionNum = 0;
        for ( TestingPlanRule rule : plan.getRules() ) {
            for ( Question question : questionDao.findRandomQuestions( rule ) ) {
                QuestionResult questionResult = new QuestionResult();
                questionResult.setQuestion( question );
                questionResult.setOrderNumber( questionNum++ );
                questionResult.setTestResult( testResult );
                questionResult.setScoreCost( rule.getScoreCost() );
                testResult.getQuestionResults().add( questionResult );
            }
        }
        testResult.setTestingPlan( plan );
        return testResult;
    }
}
