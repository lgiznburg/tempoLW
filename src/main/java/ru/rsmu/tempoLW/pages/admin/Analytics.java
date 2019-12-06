package ru.rsmu.tempoLW.pages.admin;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import ru.rsmu.tempoLW.consumabales.select_models.ExamSelectModel;
import ru.rsmu.tempoLW.consumabales.select_models.SubjectSelectModel;
import ru.rsmu.tempoLW.consumabales.select_models.TestingPlanSelectModel;
import ru.rsmu.tempoLW.dao.BaseDao;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.internal.BaseDaoImpl;
import ru.rsmu.tempoLW.dao.internal.ExamDaoImpl;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.entities.TestingPlan;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author polyakov_ps
 */

public class Analytics {

    @Property
    private List<ExamSubject> subjects;

    @Property
    @NotNull
    private ExamSubject subject;

    @InjectComponent
    private Zone testingPlanZone;

    @Property
    private SelectModel subjectModel;

    @Property
    private List<TestingPlan> plans;

    @ActivationRequestParameter
    @Property
    @NotNull
    private TestingPlan plan;

    @InjectComponent
    private Zone examScheduleZone;

    @Property
    private SelectModel planModel;

    @Property
    private List<ExamSchedule> exams;

    @InjectComponent
    private Zone submitButtonZone;

    //@InjectComponent
    //private Zone resultZone;

    @InjectComponent
    private Form filterForm;

    @ActivationRequestParameter
    @Property
    @NotNull
    private ExamSchedule exam;

    @Property
    private SelectModel examModel;

    @Property
    private Boolean show;

    @Property
    private boolean sufficientFilter;

    @Inject
    private Request request;

    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;
    @Inject
    private ExamDao examDao;

    public void setupRender() {
        subjects = examDao.findAll( ExamSubject.class );
        plans = new ArrayList<>();
        exams = new ArrayList<>();

        sufficientFilter = false;

        if ( plan == null ) { show = show == null ? false : show; }

        subjectModel = new SubjectSelectModel( subjects );
        planModel = new TestingPlanSelectModel( plans );
        examModel = new ExamSelectModel( exams );
    }

    void onValueChangedFromSubject( ExamSubject subject ) {

        if ( subject != null ) {
            this.subject = subject;

            this.plans = new ArrayList<>();
            this.exams = new ArrayList<>();
            plan = null;
            exam = null;

            List<TestingPlan> allPlans = examDao.findAll(TestingPlan.class);
            this.plans = new ArrayList<>();
            this.exams = new ArrayList<>();
            for (TestingPlan plan : allPlans) {
                if (plan.getSubject().getId() == this.subject.getId()) {
                    this.plans.add(plan);
                }
            }

            planModel = new TestingPlanSelectModel(plans);
            examModel = new ExamSelectModel( exams );

        } else {

            this.plans = new ArrayList<>();
            this.exams = new ArrayList<>();
            this.subject = null;
            plan = null;
            exam = null;

            planModel = new TestingPlanSelectModel(plans);
            examModel = new ExamSelectModel( exams );

        }

        if (request.isXHR()) {
            ajaxResponseRenderer.addRender( testingPlanZone ).addRender( examScheduleZone ).addRender( submitButtonZone );
        }

    }

    void onValueChangedFromPlan( TestingPlan plan ) {

        if ( plan != null ) {
            this.plan = plan;

            this.exams = new ArrayList<>();
            exam = null;

            List<ExamSchedule> allExams = examDao.findAll(ExamSchedule.class);
            for (ExamSchedule exam : allExams) {
                if (exam.getTestingPlan().getId() == this.plan.getId()) {
                    exams = new ArrayList<>();
                    this.exams.add(exam);
                }
            }

            examModel = new ExamSelectModel( exams );
            sufficientFilter = true;

        } else {
            this.plan = null;
            this.exams = new ArrayList<>();
            exam = null;

            //if (request.isXHR()) {
            //    ajaxResponseRenderer.addRender(submitButtonZone);
            //}

            examModel = new ExamSelectModel( exams );
        }

        if (request.isXHR()) {
            ajaxResponseRenderer.addRender(examScheduleZone).addRender(submitButtonZone);
        }
    }

    @OnEvent ( component = "filter")
    void showResults() {
        show = true;
        //ajaxResponseRenderer.addRender( resultZone );
    }

}
