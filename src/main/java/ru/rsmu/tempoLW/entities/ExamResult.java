package ru.rsmu.tempoLW.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author leonid.
 */
@Entity
@Table(name = "test_result")
public class ExamResult implements Serializable {
    private static final long serialVersionUID = -702151829561118151L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @Column(name = "start_time")
    @Temporal( TemporalType.TIMESTAMP )
    private Date startTime;

    @Column(name = "end_time")
    @Temporal( TemporalType.TIMESTAMP )
    private Date endTime;

    @Column(name = "mark_total")
    private int markTotal;

    @Column
    private String title;

    @OneToMany( mappedBy = "examResult", cascade = CascadeType.ALL )
    private List<QuestionResult> questionResults;

    @ManyToOne
    private Testee testee;

    @ManyToOne
    @JoinColumn(name = "testing_plan_id")
    private TestingPlan testingPlan;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private ExamSchedule exam;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime( Date startTime ) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime( Date endTime ) {
        this.endTime = endTime;
    }

    public int getMarkTotal() {
        return markTotal;
    }

    public void setMarkTotal( int markTotal ) {
        this.markTotal = markTotal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public List<QuestionResult> getQuestionResults() {
        return questionResults;
    }

    public void setQuestionResults( List<QuestionResult> questionResults ) {
        this.questionResults = questionResults;
    }

    public Testee getTestee() {
        return testee;
    }

    public void setTestee( Testee testee ) {
        this.testee = testee;
    }

    public TestingPlan getTestingPlan() {
        return testingPlan;
    }

    public void setTestingPlan( TestingPlan testingPlan ) {
        this.testingPlan = testingPlan;
    }

    public ExamSchedule getExam() {
        return exam;
    }

    public void setExam( ExamSchedule exam ) {
        this.exam = exam;
    }

    @Transient
    public boolean isFinished() {
        return endTime != null;
    }
}
