package ru.rsmu.tempoLW.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author leonid.
 */
public class ExamSchedule implements Serializable {
    private static final long serialVersionUID = 8540585966612131877L;

    @Column(name = "exam_date")
    @Temporal( TemporalType.DATE )
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
    private String name;

    @Column(name = "duration_hours")
    private int durationHours;

    @Column(name = "duration_minutes")
    private int durationMinutes;

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
}
