package ru.rsmu.tempoLW.pages.admin;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.datasource.ExamResultsDataSource;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.TeacherAssignment;
import ru.rsmu.tempoLW.services.SecurityUserHelper;

/**
 * @author leonid.
 * Show all works assing to the teacher for checking
 */
@RequiresRoles( value = "teacher" )
public class WorksAssignedToMe {

    @Property
    private ExamResult examResult;

    @Inject
    private ExamDao examDao;

    @Inject
    private SecurityUserHelper securityUserHelper;

    public void onCheckingFinished( ExamResult examResult ) {
        TeacherAssignment assignment = examDao.findMyAssignment( examResult, securityUserHelper.getCurrentUser()  );
        if ( assignment != null ) {
            assignment.setFinished( true );
            examDao.save( assignment );
        }

    }


    public GridDataSource getExamResultsDataSource() {
        return new ExamResultsDataSource( examDao, securityUserHelper.getCurrentUser() );
    }
}
