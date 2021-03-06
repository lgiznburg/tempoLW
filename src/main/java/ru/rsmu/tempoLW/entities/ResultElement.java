package ru.rsmu.tempoLW.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author leonid.
 */
@Entity
@Table(name = "result_element")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn( name = "type", discriminatorType = DiscriminatorType.STRING )
public abstract class ResultElement implements Serializable {
    private static final long serialVersionUID = 1122065646498916531L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @ManyToOne
    @JoinColumn(name = "question_result_id")
    private QuestionResult questionResult;

    @Column
    private boolean correct = false;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public QuestionResult getQuestionResult() {
        return questionResult;
    }

    public void setQuestionResult( QuestionResult questionResult ) {
        this.questionResult = questionResult;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect( boolean correct ) {
        this.correct = correct;
    }

    abstract public void checkCorrectness();

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !(o instanceof ResultElement) ) return false;
        ResultElement element = (ResultElement) o;
        return id == element.id &&
                correct == element.correct &&
                Objects.equals( questionResult, element.questionResult );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, questionResult, correct );
    }
}
