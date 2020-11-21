package ru.rsmu.tempoLW.services;

import ru.rsmu.tempoLW.entities.ExamSchedule;

import java.util.List;

/**
 * @author leonid.
 */
public interface TesteeCredentialsService {
    List<List<String>> createPasswordsAndEmails( ExamSchedule exam );
}
