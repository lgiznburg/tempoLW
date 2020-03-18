package ru.rsmu.tempoLW.dao;

import ru.rsmu.tempoLW.entities.*;

import java.util.List;

/**
 * @author leonid.
 */
public interface QuestionDao extends BaseDao {
    SubTopic findTopicByName( String topicTitle, ExamSubject subject );

    QuestionInfo findQuestionInfoByName( String name, SubTopic topic, int complexity );

    List<TestingPlanRule> prepareTestingPlan( ExamSubject subject );

    Question findNextQuestion( Question question );
    Question findPrevQuestion( Question question );

    List<Question> findRandomQuestions( TestingPlanRule rule );

    List<TestingPlan> findTestingPlans();
    List<TestingPlan> findTestingPlans( String language );
    List<TestingPlan> findTestingPlan( ExamSubject subject );
    List<TestingPlan> findTestingPlans( List<ExamSubject> subjects );

    List<SubTopic> findTopicsOfSubject( ExamSubject subject );

    long findQuestionsCount( ExamSubject subject );

    long findTopicsCount( ExamSubject subject );

    Question findQuestionByFilter( SubTopic topic, Integer complexity, Integer maxScore, String text );

    List<Question> findSubjectQuestions( ExamSubject subject );

    <T> List<T> findResultsOfAnswer( Class<T> resultClass, AnswerVariant answerVariant );
}
