package ru.rsmu.tempoLW.services;

import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.ExamToTestee;

import java.util.List;

/**
 * @author leonid.
 */
public interface TesteeCredentialsService {
    List<List<String>> createPasswordsAndEmails( ExamSchedule exam );

    void sendCredentialsEmail( ExamToTestee examToTestee );
}
