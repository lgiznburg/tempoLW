package ru.rsmu.tempoLW.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author leonid.
 */
@Entity
@Table(name = "question_result")
public class QuestionResult implements Serializable {
    private static final long serialVersionUID = -2651105107206732216L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @ManyToOne
    private Question question;

    @ManyToOne
    @JoinColumn(name = "test_result_id")
    private ExamResult examResult;

    @OneToMany( mappedBy = "questionResult", cascade = CascadeType.ALL )
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

    public void checkCorrectness() {
        if ( elements != null && elements.size() > 0 ) {
            elements.forEach( ResultElement::checkCorrectness );

            int errors = question.countErrors( elements );

            score = Math.max( 0, question.getQuestionInfo().getMaxScore() - errors );
            mark = score * scoreCost;
        }
    }

    @Transient
    public boolean isAnswered() {
        return elements != null && elements.size() > 0;
    }

    @Transient
    public int getViewNumber() {
        return orderNumber + 1;
    }

}
