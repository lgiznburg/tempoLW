package ru.rsmu.tempoLW.components.admin.exam;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.entities.auth.SubjectManager;
import ru.rsmu.tempoLW.entities.auth.User;
import ru.rsmu.tempoLW.entities.auth.UserRoleName;
import ru.rsmu.tempoLW.services.SecurityUserHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * List of Exams component for CRUD page
 * @author leonid.
 */
public class ExamList {

    @Parameter(required = true)
    @Property
    private Long examId;

    @Property
    private List<ExamSchedule> exams;

    @Property
    private ExamSchedule exam;

    @Inject
    private ExamDao examDao;

    @Inject
    private SecurityUserHelper securityUserHelper;


    public void setupRender() {
        User user = securityUserHelper.getCurrentUser();
        if ( user.isUserInRole( UserRoleName.admin ) ) {
            exams = examDao.findExams();
        }
        else {
            SubjectManager subjectManager = securityUserHelper.getSubjectManager( user );
            List<ExamSubject> subjects = subjectManager.getSubjects();
            exams = examDao.findExamsOfSubjects( subjects );
        }

        Comparator<ExamSchedule> examComparator = new Comparator<ExamSchedule>() {
            @Override
            public int compare(ExamSchedule o1, ExamSchedule o2) {
                return o1.getExamDate().compareTo( o2.getExamDate() );
            }
        };

        Collections.sort( exams, Collections.reverseOrder( examComparator ) );

    }

    public String getLinkCssClass() {
        if ( exam != null && examId != null && exam.getId() == examId ) {
            return "active";
        }
        return "";
    }

}
