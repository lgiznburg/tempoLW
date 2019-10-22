package ru.rsmu.tempoLW.entities;

import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
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

    @Transient
    private SubTopic topic;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable( name = "rule_topics",
            joinColumns = @JoinColumn(name = "testing_plan_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    private List<SubTopic> topics;

    /**
     * Complexity of questions
     */
    @Column
    private int complexity;

    /**
     * Max score of questions
     */
    @Column(name = "max_score",nullable = false)
    private int maxScore;

    @Column(name = "question_count")
    private int questionCount;

    // Cost of one score of question
    @Column(name = "score_cost")
    private int scoreCost;

    @Transient
    private long totalQuestions;

    @Formula( "max_score * score_cost" )
    private int maxMark;

    @Formula( "question_count * score_cost * max_score" )
    private int totalRuleMark;

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

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore( int maxScore ) {
        this.maxScore = maxScore;
    }

    public int getMaxMark() {
        return maxMark;
    }

    public int getTotalRuleMark() {
        //this.totalRuleMark = questionCount * maxMark;
        return totalRuleMark;
    }

    public void setTotalRuleMark(int totalRuleMark) { this.totalRuleMark = totalRuleMark; }

    public void setMaxMark(int maxMark ) {
        this.maxMark = maxMark;
    }

    public List<SubTopic> getTopics() {
        return topics;
    }

    public void setTopics( List<SubTopic> topics ) {
        this.topics = topics;
    }

    @Override
    public int compareTo( TestingPlanRule o ) {
        int firstCompare = this.complexity - o.complexity;
        if ( firstCompare != 0 ) return firstCompare;
        int secondCompare = this.maxScore - o.maxScore;
        return  (secondCompare == 0) ? ( this.getTopics().size() - o.getTopics().size() ) : secondCompare;
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
                //Objects.equals( topic, that.topic ) &&
                // long and complicates comparison of two lists
                // check they both are null or contains the same elements
                (
                    (topics == null && that.topics == null) ||
                    (
                        topics != null &&
                        that.topics != null &&
                        topics.containsAll( that.topics ) &&
                        that.topics.containsAll( topics )
                    )
                );
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash( id, testingPlan, /*topic,*/ complexity, questionCount, scoreCost );
        if ( topics != null ) {
            for ( SubTopic topic : topics ) {
                hash = 31 * hash + topic.hashCode();
            }
        }
        return hash;
    }

/*
    @Override
    public String toString() {
        return String.format( "%s (%d-%d)", topic.getTitle(), complexity, maxScore );
    }
*/
}
