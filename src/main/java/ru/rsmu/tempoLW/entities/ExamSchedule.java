package ru.rsmu.tempoLW.entities;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author leonid.
 */
@Entity
@Table(name = "exam_schedule")
public class ExamSchedule implements Serializable {
    private static final long serialVersionUID = 8540585966612131877L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @Column(name = "exam_date")
    @Temporal( TemporalType.DATE )
    @NotNull
    private Date examDate;

    @ManyToOne
    @JoinColumn(name = "testing_plan_id")
    @NotNull
    private TestingPlan testingPlan;

    @ManyToMany( mappedBy = "exam", cascade = CascadeType.ALL)
    private List<ExamToTestee> examToTestees;

    @Column
    @NotEmpty
    private String name;

    @Column(name = "duration_hours")
    private int durationHours;

    @Column(name = "duration_minutes")
    private int durationMinutes;

    @Column(name = "use_proctoring")
    private boolean useProctoring;

    @Column(name = "period_start_time")
    @Temporal( TemporalType.TIMESTAMP )
    private Date periodStartTime;

    @Column(name = "period_end_time")
    @Temporal( TemporalType.TIMESTAMP )
    private Date periodEndTime;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate( Date examDate ) {
        this.examDate = examDate;
    }

    public TestingPlan getTestingPlan() {
        return testingPlan;
    }

    public void setTestingPlan( TestingPlan testingPlan ) {
        this.testingPlan = testingPlan;
    }

/*
    public List<Testee> getTestees() {
        return testees;
    }
*/

    public void setTestees( List<Testee> testees ) {
        //this.testees = testees;
        addTestees( testees );
    }

    /** add single testee to schedule */
    public void addTestee (Testee testee) {
        List<Testee> testees = new ArrayList<>();
        testees.add( testee );
        addTestees( testees );
    }

    public List<ExamToTestee> getExamToTestees() {
        return examToTestees;
    }

    public void setExamToTestees( List<ExamToTestee> examToTestees ) {
        this.examToTestees = examToTestees;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public int getDurationHours() {
        return durationHours;
    }

    public void setDurationHours( int durationHours ) {
        this.durationHours = durationHours;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes( int durationMinutes ) {
        this.durationMinutes = durationMinutes;
    }

    public boolean isUseProctoring() {
        return useProctoring;
    }

    public void setUseProctoring( boolean useProctoring ) {
        this.useProctoring = useProctoring;
    }

    public Date getPeriodStartTime() {
        return periodStartTime;
    }

    public void setPeriodStartTime( Date periodStartTime ) {
        this.periodStartTime = periodStartTime;
    }

    public Date getPeriodEndTime() {
        return periodEndTime;
    }

    public void setPeriodEndTime( Date periodEndTime ) {
        this.periodEndTime = periodEndTime;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        ExamSchedule that = (ExamSchedule) o;
        return id == that.id &&
                durationHours == that.durationHours &&
                durationMinutes == that.durationMinutes &&
                examDate.equals( that.examDate ) &&
                testingPlan.equals( that.testingPlan ) &&
                name.equals( that.name );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, examDate, testingPlan, name, durationHours, durationMinutes );
    }

    private void addTestees( List<Testee> testees ) {
        for ( Testee testee : testees ) {
            if ( examToTestees == null ) {
                examToTestees = new ArrayList<>();
            }
            ExamToTestee examToTestee = null;
            for ( ExamToTestee existed : examToTestees ) {
                if ( existed.getTestee().equals( testee ) ) {
                    examToTestee = existed;
                    break;
                }
            }
            if ( examToTestee != null ) {
                continue;
            }
            examToTestee = new ExamToTestee();
            examToTestee.setExam( this );
            examToTestee.setTestee( testee );
            this.examToTestees.add( examToTestee );
        }
    }
}
