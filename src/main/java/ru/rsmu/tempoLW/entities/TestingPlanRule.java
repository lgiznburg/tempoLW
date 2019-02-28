package ru.rsmu.tempoLW.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

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

    // Cost of one score of question
    @Column(name = "score_cost")
    private int scoreCost;

    @Transient
    private long totalQuestions;

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

    public long getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions( long totalQuestions ) {
        this.totalQuestions = totalQuestions;
    }

    public int getScoreCost() {
        return scoreCost;
    }

    public void setScoreCost( int scoreCost ) {
        this.scoreCost = scoreCost;
    }

    @Override
    public int compareTo( TestingPlanRule o ) {
        int firstCompare = this.complexity - o.complexity;

        return  (firstCompare == 0) ? this.getTopic().getTitle().compareTo( o.getTopic().getTitle() ) : firstCompare;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        TestingPlanRule that = (TestingPlanRule) o;
        return id == that.id &&
                complexity == that.complexity &&
                questionCount == that.questionCount &&
                scoreCost == that.scoreCost &&
                Objects.equals( testingPlan, that.testingPlan ) &&
                Objects.equals( topic, that.topic );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, testingPlan, topic, complexity, questionCount, scoreCost );
    }
}
