package ru.rsmu.tempoLW.components.admin.exam;

import org.apache.tapestry5.*;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.util.AbstractSelectModel;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.entities.TestingPlan;
import ru.rsmu.tempoLW.entities.auth.SubjectManager;
import ru.rsmu.tempoLW.entities.auth.User;
import ru.rsmu.tempoLW.entities.auth.UserRoleName;
import ru.rsmu.tempoLW.services.SecurityUserHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * Edit component for ExamSchedule
 * @author leonid.
 */
public class ExamEdit {
    @Property
    @Parameter(required = true)
    private Long examId;

    @Property
    private ExamSchedule exam;

    @Property
    private SelectModel testingPlanModel;

    @Inject
    private ExamDao examDao;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private ComponentResources componentResources;

    @InjectComponent
    private Form examForm;

    @Inject
    private SelectModelFactory modelFactory;

    @Inject
    private SecurityUserHelper securityUserHelper;

    public void onPrepareForRender() {
        if ( examForm.isValid() ) {
            prepare();
        }
    }

    public void onPrepareForSubmit() {
        prepare();
    }

    private void prepare() {
        if ( examId != null ) {
            exam = examDao.find( ExamSchedule.class, examId );
        }
        if ( exam == null ) {
            exam = new ExamSchedule();
        }
        testingPlanModel = new AbstractSelectModel() {
            @Override
            public List<OptionGroupModel> getOptionGroups() {
                return null;
            }

            @Override
            public List<OptionModel> getOptions() {
                List<TestingPlan> plans;
                User user = securityUserHelper.getCurrentUser();
                if ( user.isUserInRole( UserRoleName.admin ) ) {
                    plans = questionDao.findTestingPlans();
                }
                else {
                    SubjectManager subjectManager = securityUserHelper.getSubjectManager( user );
                    List<ExamSubject> subjects = subjectManager.getSubjects();
                    plans = questionDao.findTestingPlans( subjects );
                }
                List<OptionModel> options = new LinkedList<>();
                for ( TestingPlan plan : plans ) {
                    options.add( new OptionModelImpl( plan.getSubject().getTitle() + " (" + plan.getName() + ")", plan ) );
                }
                return options;
            }
        };
    }

    public boolean onSuccess() {
        String event = exam.getId() == 0 ? "examCreated" : "examUpdated";
        examDao.save( exam );
        componentResources.triggerEvent( event, new Object[]{ exam.getId() }, null );
        return true;
    }

    public boolean isEdit() {
        return exam.getId() != 0;
    }

    public ValueEncoder<TestingPlan> getTestingPlanEncoder() {
        return new ValueEncoder<TestingPlan>() {
            @Override
            public String toClient( TestingPlan plan ) {
                return String.valueOf( plan.getId() );
            }

            @Override
            public TestingPlan toValue( String clientValue ) {
                Long id = Long.parseLong( clientValue );
                return examDao.find( TestingPlan.class, id );
            }
        };
    }
}
