package ru.rsmu.tempoLW.dao;

import ru.rsmu.tempoLW.entities.*;

import java.util.List;

/**
 * @author leonid.
 */
public interface QuestionDao extends BaseDao {
    SubTopic findTopicByName( String topicTitle );

    QuestionInfo findQuestionInfoByName( String name, SubTopic topic, int complexity );

    List<TestingPlanRule> prepareTestingPlan( TestSubject subject );

    Question findNextQuestion( long id, TestSubject subject );
    Question findPrevQuestion( long id, TestSubject subject );

    Question findRandomQuestion( TestingPlanRule rule );
    List<Question> findRandomQuestions( TestingPlanRule rule );

    List<TestingPlan> findTestingPlans();

    List<SubTopic> findTopicsOfSubject( TestSubject subject );
}
