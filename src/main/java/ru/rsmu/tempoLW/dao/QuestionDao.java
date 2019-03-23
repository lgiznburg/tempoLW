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

    Question findNextQuestion( long id, ExamSubject subject );
    Question findPrevQuestion( long id, ExamSubject subject );

    List<Question> findRandomQuestions( TestingPlanRule rule );

    List<TestingPlan> findTestingPlans();
    List<TestingPlan> findTestingPlan( ExamSubject subject );

    List<SubTopic> findTopicsOfSubject( ExamSubject subject );

    long findQuestionsCount( ExamSubject subject );

    long findTopicsCount( ExamSubject subject );
}
