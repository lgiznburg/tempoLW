package ru.rsmu.tempoLW.dao;

import ru.rsmu.tempoLW.entities.*;

import java.util.Date;
import java.util.List;

/**
 * @author leonid.
 */
public interface ExamDao extends BaseDao {
    List<ExamSchedule> findExamsOfSubjects( List<ExamSubject> subjects );
    List<ExamSchedule> findExamsOfSubjects( List<ExamSubject> subjects, Date date );

    List<ExamSchedule> findExams();
    List<ExamSchedule> findExams( Date date );

    ExamSchedule findExamToday();
    ExamToTestee findExamForTestee( Testee testee, String password );

    List<ExamResult> findExamResults( ExamSchedule exam );

    ExamResult findExamResultForTestee( ExamSchedule exam, Testee testee );
    List<ExamResult> findResultsForTestee( Testee testee );

    List<ExamResult> findExamResultsForSubject( ExamSubject subject );

    ProctoringReport findProctoringReport( String id );

    List<ProctoringReport> findProctoringForExam( ExamSchedule exam );

    List<ExamSchedule> findAllExamsToday();

    List<ExamResult> findOldExamResults( ExamSchedule examSchedule, Date time );
}
