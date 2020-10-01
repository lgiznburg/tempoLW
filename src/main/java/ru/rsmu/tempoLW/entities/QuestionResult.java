package ru.rsmu.tempoLW.entities;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author leonid.
 */
@Entity
@Table(name = "question_result")
public class QuestionResult implements Serializable, Comparable<QuestionResult> {
    private static final long serialVersionUID = -2651105107206732216L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @ManyToOne
    private Question question;

    @ManyToOne
    @JoinColumn(name = "test_result_id")
    private ExamResult examResult;

    @OneToMany( mappedBy = "questionResult", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @LazyCollection( LazyCollectionOption.TRUE )
    private List<ResultElement> elements;

    // result for this question
    @Column
    private int score = 0;

    @Column( name = "score_cost")
    private int scoreCost = 1;

    // mark for this question ( multiple of score and score_cost )
    @Column
    private int mark = 0;

    @Column(name = "order_number")
    private int orderNumber;

    @Column(name = "updated")
    @Temporal( TemporalType.TIMESTAMP )
    private Date updated;

    @Formula( "(select count(*) from result_element re where re.question_result_id = id)" )
    private int answeredCount;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion( Question question ) {
        this.question = question;
    }

    public ExamResult getExamResult() {
        return examResult;
    }

    public void setExamResult( ExamResult examResult ) {
        this.examResult = examResult;
    }

    public List<ResultElement> getElements() {
        return elements;
    }

    public void setElements( List<ResultElement> elements ) {
        this.elements = elements;
    }

    public int getScore() {
        return score;
    }

    public void setScore( int score ) {
        this.score = score;
    }

    public int getScoreCost() {
        return scoreCost;
    }

    public void setScoreCost( int scoreCost ) {
        this.scoreCost = scoreCost;
    }

    public int getMark() {
        return mark;
    }

    public void setMark( int mark ) {
        this.mark = mark;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber( int orderNumber ) {
        this.orderNumber = orderNumber;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated( Date updated ) {
        this.updated = updated;
    }

    //@Deprecated
    // it could cause LazyInitException for Tree and Correspondence
    public void checkCorrectness() {
        if ( elements != null && elements.size() > 0 ) {
            elements.forEach( ResultElement::checkCorrectness );

            int errors = question.countErrors( elements );

            score = Math.max( 0, question.getQuestionInfo().getMaxScore() - errors );
            mark = score * scoreCost;
        }
    }

    public int getAnsweredCount() {
        return answeredCount;
    }

    public void setAnsweredCount( int answeredCount ) {
        this.answeredCount = answeredCount;
    }

    @Transient
    public boolean isAnswered() {
        return answeredCount > 0;
    }

    @Transient
    public int getViewNumber() {
        return orderNumber + 1;
    }

    @Override
    public int compareTo( QuestionResult o ) {
        return orderNumber - o.getOrderNumber();
    }
}
