package ru.rsmu.tempoLW.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author leonid.
 */
@Entity
@Table(name = "testing_plan_rule")
public class TestingPlanRule implements Serializable, Comparable<TestingPlanRule> {
    private static final long serialVersionUID = -2684661248495710807L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @ManyToOne
    @JoinColumn(name = "testing_plan_id")
    private TestingPlan testingPlan;

    @ManyToOne
    private SubTopic topic;

    @Column
    private int complexity;

    @Column(name = "question_count")
    private int questionCount;

    @Transient
    private int totalQuestions;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public TestingPlan getTestingPlan() {
        return testingPlan;
    }

    public void setTestingPlan( TestingPlan testingPlan ) {
        this.testingPlan = testingPlan;
    }

    public SubTopic getTopic() {
        return topic;
    }

    public void setTopic( SubTopic topic ) {
        this.topic = topic;
    }

    public int getComplexity() {
        return complexity;
    }

    public void setComplexity( int complexity ) {
        this.complexity = complexity;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount( int questionCount ) {
        this.questionCount = questionCount;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions( int totalQuestions ) {
        this.totalQuestions = totalQuestions;
    }

    @Override
    public int compareTo( TestingPlanRule o ) {
        int firstCompare = this.complexity - o.complexity;

        return  (firstCompare == 0) ? this.getTopic().getTitle().compareTo( o.getTopic().getTitle() ) : firstCompare;
    }
}
