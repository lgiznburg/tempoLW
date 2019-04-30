package ru.rsmu.tempoLW.entities;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
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

    @ManyToMany
    @JoinTable( name = "exam_testees",
            joinColumns = @JoinColumn(name = "exam_id"),
            inverseJoinColumns = @JoinColumn(name = "testee_id")
    )
    private List<Testee> testees;

    @Column
    @NotEmpty
    private String name;

    @Column(name = "duration_hours")
    private int durationHours;

    @Column(name = "duration_minutes")
    private int durationMinutes;

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

    public List<Testee> getTestees() {
        return testees;
    }

    public void setTestees( List<Testee> testees ) {
        this.testees = testees;
    }

    /** add single testee to schedule */
    public void addTestee (Testee testee) {
        this.testees.add(testee);
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
}
