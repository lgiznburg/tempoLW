package ru.rsmu.tempoLW.components.admin;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.entities.TestingPlan;

import java.util.List;

/**
 * @author leonid.
 */
public class SubjectReview {
    @Parameter(required = true)
    @Property
    private Long subjectId;


    // screen feilds
    @Property
    private ExamSubject subject;

    @Property
    private List<TestingPlan> plans;

    @Property
    private TestingPlan plan;


    // other
    @Inject
    private QuestionDao questionDao;

    public void setupRender() {
        subject = subjectId != null ? questionDao.find( ExamSubject.class, subjectId ) : null;

        if ( subject != null ) {
            plans = questionDao.findTestingPlan( subject );
        }
    }

    public long getQuestionsCount() {
        if ( subject != null ) {
            return  questionDao.findQuestionsCount( subject );
        }
        return 0;
    }

    public long getTopicsCount() {
        if ( subject != null ) {
            return  questionDao.findTopicsCount( subject );
        }
        return 0;

    }
}
