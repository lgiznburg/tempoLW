package ru.rsmu.tempoLW.dao;

import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.entities.Testee;

import java.util.List;

/**
 * @author leonid.
 */
public interface ExamDao extends BaseDao {
    List<ExamSchedule> findExamsOfSubjects( List<ExamSubject> subjects );

    List<ExamSchedule> findExams();

    ExamSchedule findExamToday();
    ExamSchedule findExamForTestee( Testee testee );

    List<ExamResult> findExamResults( ExamSchedule exam );

    ExamResult findExamResultForTestee( ExamSchedule exam, Testee testee );
}
