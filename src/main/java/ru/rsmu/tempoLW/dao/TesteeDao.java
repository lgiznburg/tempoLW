package ru.rsmu.tempoLW.dao;

import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.Testee;

/**
 * @author leonid.
 */
public interface TesteeDao extends BaseDao {
    Testee findByName( String testeeName );

    Testee findByCaseNumber( String caseNumber );

    public Boolean isTesteeInExam ( String caseNumber, ExamSchedule exam );
}
