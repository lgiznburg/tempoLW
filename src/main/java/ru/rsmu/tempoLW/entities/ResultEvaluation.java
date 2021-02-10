package ru.rsmu.tempoLW.entities;

import ru.rsmu.tempoLW.entities.auth.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author leonid.
 */
@Entity
@Table(name="result_evaluations")
public class ResultEvaluation implements Serializable {
    private static final long serialVersionUID = -5699445194634164758L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String explanation = "";

    @Column
    private int score = 0;

    @ManyToOne
    @JoinColumn(name = "result_element_id")
    private ResultElement resultElement;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User examiner;

    @Column(name = "updated_at")
    @Temporal( TemporalType.TIMESTAMP )
    @NotNull
    private Date updated;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation( String explanation ) {
        this.explanation = explanation;
    }

    public ResultElement getResultElement() {
        return resultElement;
    }

    public void setResultElement( ResultElement resultElement ) {
        this.resultElement = resultElement;
    }

    public User getExaminer() {
        return examiner;
    }

    public void setExaminer( User examiner ) {
        this.examiner = examiner;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated( Date updated ) {
        this.updated = updated;
    }

    public int getScore() {
        return score;
    }

    public void setScore( int score ) {
        this.score = score;
    }
}

