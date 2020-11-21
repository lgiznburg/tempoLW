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

    /**
     * Find all exams
     * @return All exams
     */
    List<ExamSchedule> findExams();

    /**
     * Find exams starting from the year of the date
     * @param date Date to define a year
     * @return All exams of the year
     */
    List<ExamSchedule> findExams( Date date );

    /**
     * Find all upcoming exams starting from current date
     * @return All upcoming exams
     */
    List<ExamSchedule> findUpcomingExams();

    ExamSchedule findExamToday();
    ExamToTestee findExamForTestee( Testee testee, String password );

    List<ExamResult> findExamResults( ExamSchedule exam );

    ExamResult findExamResultForTestee( ExamSchedule exam, Testee testee );
    List<ExamResult> findResultsForTestee( Testee testee );

    List<ExamResult> findExamResultsForSubject( ExamSubject subject );

    ProctoringReport findProctoringReport( String id );

    List<ProctoringReport> findProctoringForExam( ExamSchedule exam );

    /**
     * Find all exams for Cleanup. Exam has been already started and it's end time is no more than an hour ago
     * @return All exams for cleanup.
     */
    List<ExamSchedule> findAllExamsToday();
    List<ExamSchedule> findRunningExams();

    List<ExamResult> findOldExamResults( ExamSchedule examSchedule, Date time );

}
