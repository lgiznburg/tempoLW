package ru.rsmu.tempoLW.components.admin.exam;

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
    private ExamSchedule exam;

    @InjectComponent
    private Form addTesteeForm;

    @InjectComponent
    private TextField lastname;

    @InjectComponent
    private TextField firstname;

    @InjectComponent
    private TextField middlename;

    @InjectComponent
    private TextField casenumber;

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

    boolean onValidateFromAddTesteeForm() {
        if ( this.caseNumber != null && !this.lastName.isEmpty() && !this.firstName.isEmpty() && !this.middleName.isEmpty() ) {
            this.exam = examDao.find( ExamSchedule.class, examId );
            if ( !isTesteeInExam() && !testeeAlreadyExists() ) {
                this.testee = new Testee();
                TesteeLoader tloader = new TesteeLoader(testeeDao);
                testee.setCaseNumber(caseNumber);
                testee.setLastName(lastName + " " + firstName + " " + middleName);
                testee.setLogin(tloader.createLogin(caseNumber));
            } else if ( !isTesteeInExam() && testeeAlreadyExists() ) {
                this.testee = testeeDao.findByCaseNumber(caseNumber);
            } else {
                addTesteeForm.recordError(messages.get("testee-already-exists"));
            }
        } else {
            addTesteeForm.recordError(messages.get("add-testee-incomplete"));
        }

        return true;
    }

    boolean onSuccessFromAddTesteeForm() {
        String event = "testeeAdded";
        if ( !testeeAlreadyExists() ) {
            testeeDao.save(testee);
        }
        this.exam = examDao.find( ExamSchedule.class, examId );
        exam.addTestee(testee);
        examDao.save( exam );

        componentResources.triggerEvent( event, new Object[]{ exam.getId() }, null );

        return true;
    }

    public Boolean isTesteeInExam () {
        List<Testee> testees = exam.getTestees();
        if(testees.size() != 0) {
            for (Testee testee : testees) {
                if (testee.getCaseNumber().equals(caseNumber)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean testeeAlreadyExists () {
        Testee testee = testeeDao.findByCaseNumber(caseNumber);
        if( testee != null ) {
            return true;
        }
        return false;

    }


}
