package ru.rsmu.tempoLW.pages.admin.testingplan;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.hibernate.HibernateGridDataSource;
import org.hibernate.Session;
import ru.rsmu.tempoLW.entities.TestingPlan;

import javax.inject.Inject;

/**
 * @author leonid.
 */
public class TestingPlanList {
    @Inject
    private Session session;

    @Property
    private GridDataSource planSource = new HibernateGridDataSource( session, TestingPlan.class );

    @Property
    private TestingPlan plan;


}
