package ru.rsmu.tempoLW.services.impl;

import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.entities.QuestionResult;
import ru.rsmu.tempoLW.entities.ResultElement;
import ru.rsmu.tempoLW.services.CorrectnessService;

/**
 * @author leonid.
 */
public class CorrectnessServiceImpl implements CorrectnessService {

    @Inject
    private QuestionDao questionDao;

    public void checkCorrectness( QuestionResult questionResult ) {
        if ( questionResult.getElements() != null && questionResult.getElements().size() > 0 ) {
            questionResult.getElements().forEach( ResultElement::checkCorrectness );

            Question question = questionDao.find( Question.class, questionResult.getQuestion().getId() );
            int errors = question.countErrors( questionResult.getElements() );

            questionResult.setScore( Math.max( 0, question.getQuestionInfo().getMaxScore() - errors ) );
            questionResult.setMark( questionResult.getScore() * questionResult.getScoreCost() );
            //score = Math.max( 0, question.getQuestionInfo().getMaxScore() - errors );
            //mark = score * scoreCost;
        }

    }
}
