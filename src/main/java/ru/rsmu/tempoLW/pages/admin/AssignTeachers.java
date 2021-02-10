package ru.rsmu.tempoLW.pages.admin;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ValueEncoderSource;
import ru.rsmu.tempoLW.consumabales.select_models.ExamSelectModel;
import ru.rsmu.tempoLW.consumabales.select_models.UserSelectModel;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.UserDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.TeacherAssignment;
import ru.rsmu.tempoLW.entities.auth.User;
import ru.rsmu.tempoLW.entities.auth.UserRoleName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author leonid.
 */
@RequiresRoles( value = {"admin","subject_admin"}, logical = Logical.OR )
public class AssignTeachers {

    @Property
    private UserSelectModel teachersModel;

    @Property
    private List<User> selectedTeachers;

    @Property
    private ExamSelectModel examsModel;

    @Property
    private List<ExamSchedule> selectedExams;

    @Property
    @Persist(PersistenceConstants.FLASH)
    private String message;

    //==========
    @Inject
    private ValueEncoderSource valueEncoderSource;

    @Inject
    private ExamDao examDao;

    @Inject
    private UserDao userDao;

    @Inject
    private Messages messages;


    public void onActivate() {
        selectedTeachers = new ArrayList<>();
        selectedExams = new ArrayList<>();

        teachersModel = new UserSelectModel( userDao.finUserForRole( UserRoleName.teacher ) );
        examsModel = new ExamSelectModel( examDao.findExams( new Date() ) );  //  all exams of this year

    }

    public void onSuccess() {
        if ( selectedExams.size() > 0 && selectedTeachers.size() > 0 ) {
            List<ExamResult> allResults = examDao.findNotAssignedResults( selectedExams );

            int resultsToTeacher = allResults.size() / selectedTeachers.size();
            int extraNum = allResults.size() % selectedTeachers.size();

            int startIndex = 0;
            int endIndex = 0;
            for ( User teacher : selectedTeachers ) {
                startIndex = endIndex;
                endIndex += resultsToTeacher;
                if ( extraNum-- > 0 ) {
                    endIndex++;
                }
                if ( endIndex > allResults.size() ) {
                    endIndex = allResults.size();
                }
                for ( ExamResult result : allResults.subList( startIndex, endIndex ) ) {
                    TeacherAssignment assignment = new TeacherAssignment();
                    assignment.setExamResult( result );
                    assignment.setTeacher( teacher );
                    examDao.save( assignment );
                }
            }
            message = messages.format( "result-of-assign", allResults.size(), selectedTeachers.size() );
        }
    }


    public ValueEncoder<User> getTeacherEncoder() {
        return valueEncoderSource.getValueEncoder( User.class );
    }

    public ValueEncoder<ExamSchedule> getExamEncoder() {
        return valueEncoderSource.getValueEncoder( ExamSchedule.class );
    }
}
