package ru.rsmu.tempoLW.pages.admin;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.hibernate.HibernateGridDataSource;
import org.hibernate.Session;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.TestSubject;

import javax.inject.Inject;

/**
 * @author leonid.
 */
@RequiresRoles(value = {"admin","subject_admin"}, logical = Logical.OR )
public class Subjects {
    @Inject
    private Session session;

    @Property
    private GridDataSource subjectSource = new HibernateGridDataSource( session, TestSubject.class );

    @Property
    private TestSubject currentSubject;

    @InjectComponent
    private Grid subjectTable;

}
