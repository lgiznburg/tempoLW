package ru.rsmu.tempoLW.components.admin.exam;

import org.apache.tapestry5.*;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ValueEncoderSource;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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

    // proxy date - to fix tapestry issue with datefield and timezone
    @Property
    private Date proxyDate;

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

    @Inject
    private ValueEncoderSource valueEncoderSource;

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

        /* Tapestry datefield component uses milliseconds for communicate with JS
         *  so we need to use DateFormat for GMT timezone.
         *  To match SQL date (exam.examDate) with no time and timezone
         *  it needs to be moved to MSK timezone. Craziness
         */
        Calendar proxyCalendar = Calendar.getInstance();
        if ( exam.getExamDate() != null ) {
            proxyCalendar.setTimeInMillis( exam.getExamDate().getTime() + proxyCalendar.get( Calendar.ZONE_OFFSET ) );
        }
        proxyDate = proxyCalendar.getTime();

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
        exam.setExamDate( proxyDate );
        examDao.save( exam );
        componentResources.triggerEvent( event, new Object[]{ exam.getId() }, null );
        return true;
    }

    public boolean isEdit() {
        return exam.getId() != 0;
    }

    public ValueEncoder<TestingPlan> getTestingPlanEncoder() {
        return valueEncoderSource.getValueEncoder( TestingPlan.class );
    }

    public DateFormat getCorrectDateFormat() {
        // to correct work of datefield we need to use Date Format of GMT
        SimpleDateFormat format = new SimpleDateFormat( "dd/MM/yyyy" );
        format.setTimeZone( TimeZone.getTimeZone("GMT") );
        return format;
    }
}
