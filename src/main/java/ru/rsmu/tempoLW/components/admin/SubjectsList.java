package ru.rsmu.tempoLW.components.admin;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.entities.auth.SubjectManager;
import ru.rsmu.tempoLW.entities.auth.User;
import ru.rsmu.tempoLW.entities.auth.UserRoleName;
import ru.rsmu.tempoLW.services.SecurityUserHelper;

import java.util.List;

/**
 * @author leonid.
 */
public class SubjectsList {
    @Parameter(required = true)
    @Property
    private Long subjectId;

    @Property
    private List<ExamSubject> subjects;

    @Property
    private ExamSubject subject;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private SecurityUserHelper securityUserHelper;

    public void setupRender() {
        User user = securityUserHelper.getCurrentUser();
        if ( user.isUserInRole( UserRoleName.admin ) ) {
            subjects = questionDao.findAll( ExamSubject.class );
        }
        else {
            SubjectManager subjectManager = securityUserHelper.getSubjectManager( user );
            subjects = subjectManager.getSubjects();
        }
    }

    public String getLinkCssClass() {
        if ( subject != null && subjectId != null && subject.getId() == subjectId ) {
            return "active";
        }
        return "";
    }
}
