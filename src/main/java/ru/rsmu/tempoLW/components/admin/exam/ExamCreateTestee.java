package ru.rsmu.tempoLW.components.admin.exam;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.TesteeDao;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.ExamToTestee;
import ru.rsmu.tempoLW.entities.Testee;
import ru.rsmu.tempoLW.utils.TesteeLoader;

import java.util.List;

@RequiresRoles(value = "admin" )
public class ExamCreateTestee {
    @Parameter(required = true)
    @Property
    private Long examId;

    @Property
    private Testee testee;

    @Property
    private String lastName;

    @Property
    private String firstName;

    @Property
    private String middleName;

    @Property
    private String caseNumber;

    @Property
    private String email;

    @Property
    private ExamSchedule exam;

    @InjectComponent
    private Form addTesteeForm;

/*
    @InjectComponent
    private TextField lastname;

    @InjectComponent
    private TextField firstname;

    @InjectComponent
    private TextField middlename;

    @InjectComponent
    private TextField casenumber;

    @InjectComponent
    private TextField emailField;
*/

    @Inject
    private ExamDao examDao;

    @Inject
    private TesteeDao testeeDao;

    @Inject
    private Messages messages;

    @Inject
    private ComponentResources componentResources;

    void setupRender() {
        // If fresh start, make sure there's a User object available.
        this.exam = examDao.find( ExamSchedule.class, examId );
    }

    public void onPrepareForRender() {
        if ( addTesteeForm.isValid() ) {
            prepare();
        }
    }

    public void onPrepareForSubmit() {
        prepare();
    }

    public void prepare () {
        this.exam = examDao.find( ExamSchedule.class, examId );
        this.testee = testeeDao.findByCaseNumber(caseNumber);
        if ( this.testee == null ) {
            this.testee = new Testee();
        }
    }

    boolean onValidateFromAddTesteeForm() {
        if ( StringUtils.isNotBlank( caseNumber )  ) {

            Testee existedOne = testeeDao.findByCaseNumber( caseNumber );
            if ( existedOne != null ) {
                testee = existedOne;
            }
            if ( testee.getId() == 0 ) {
                if ( StringUtils.isNotBlank( lastName ) ) {
                    testee.setCaseNumber( caseNumber );
                    StringBuilder builder = new StringBuilder( lastName );
                    if ( StringUtils.isNotBlank( firstName ) ) {
                        builder.append( " " ).append( firstName );
                    }
                    if ( StringUtils.isNotBlank( middleName ) ) {
                        builder.append( " " ).append( middleName );
                    }
                    testee.setLastName( builder.toString() );
                    testee.setLogin( new TesteeLoader( testeeDao ).createLogin( caseNumber ) );
                    testee.setEmail( email );
                }
                else {
                    addTesteeForm.recordError(messages.get("add-testee-incomplete"));
                }
            }
            if ( isTesteeInExam() ) {
                addTesteeForm.recordError(messages.get("testee-already-exists"));
            }
        } else {
            addTesteeForm.recordError(messages.get("add-testee-incomplete"));
        }

        return true;
    }

    boolean onSuccessFromAddTesteeForm() {
        String event = "testeeAdded";
        if ( testee.getId() == 0 ) { //new testee
            testeeDao.save(testee);
        }
        //this.exam = examDao.find( ExamSchedule.class, examId );
        exam.addTestee(testee);
        examDao.save( exam );

        componentResources.triggerEvent( event, new Object[]{ exam.getId() }, null );

        return true;
    }

    private Boolean isTesteeInExam() {
        List<ExamToTestee> testees = exam.getExamToTestees();
        if(testees.size() != 0) {
            for (ExamToTestee examToTestee : testees) {
                if (examToTestee.getTestee().getCaseNumber().equals(caseNumber)) {
                    return true;
                }
            }
        }
        return false;
    }

}
