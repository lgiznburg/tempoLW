package ru.rsmu.tempoLW.services;

import ru.rsmu.tempoLW.entities.QuestionResult;

/**
 * @author leonid.
 */
public interface CorrectnessService {

    void checkCorrectness( QuestionResult questionResult );
}
