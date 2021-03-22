package ru.rsmu.tempoLW.components.admin.exam;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.QuestionResult;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Review Exam component for CRUD
 * @author leonid.
 */
public class ExamReview {
    @Parameter(required = true)
    @Property
    private Long examId;

    @Property
    private ExamSchedule exam;

    @Inject
    private ExamDao examDao;

    public void setupRender() {
        exam = examId != null ? examDao.find( ExamSchedule.class, examId ) : null;
    }

    public boolean isBeforeExamDate() {
        return exam != null && exam.getExamDate().after( new Date() );
    }

    public boolean isAtExamDate() {
        if ( exam != null ) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime( exam.getExamDate() );
            calendar.add( Calendar.DAY_OF_YEAR, 1 );
            return calendar.getTime().after( new Date() );
        }
        return false;
    }

    public boolean isAfterExamDate() {
        return exam != null && exam.getExamDate().before( new Date() );
    }

    public boolean onRecalculate() {
        if ( examId != null ) {
            exam = examDao.find( ExamSchedule.class, examId );
            if ( exam != null ) {
                List<ExamResult> results = examDao.findExamResults( exam );
                results.forEach( examResult -> {
                    examResult.getQuestionResults().forEach( qr -> {
                        if ( !qr.getQuestion().isManualCheckingRequired() ) qr.checkCorrectness();
                    } );
                    int finalMark = examResult.getQuestionResults().stream().mapToInt( QuestionResult::getMark ).sum();
                    examResult.setMarkTotal( finalMark );
                    examDao.save( examResult );
                } );
            }
        }

        return true;
    }
}
