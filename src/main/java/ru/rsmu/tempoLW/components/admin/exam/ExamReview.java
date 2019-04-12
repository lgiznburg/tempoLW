package ru.rsmu.tempoLW.components.admin.exam;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.entities.ExamSchedule;

import java.util.Date;

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

    public boolean beforeExam() {
        return exam.getExamDate().before( new Date() );
    }
}
