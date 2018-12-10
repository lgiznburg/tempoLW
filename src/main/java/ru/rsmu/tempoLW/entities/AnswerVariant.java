package ru.rsmu.tempoLW.entities;

import org.apache.tapestry5.beaneditor.Validate;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author leonid.
 */
@Entity
@Table(name = "answer_variant")
public class AnswerVariant implements Serializable, Comparable<AnswerVariant> {
    private static final long serialVersionUID = 5012066124661313001L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @Column
    @Validate( "required" )
    private String text = "";

    @Column
    private boolean correct = false;

    @ManyToOne
    private Question question;

    @Transient
    private int sortOrder = 0;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect( boolean correct ) {
        this.correct = correct;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion( Question question ) {
        this.question = question;
    }

    @Override
    public int compareTo( AnswerVariant o ) {
        if ( sortOrder == 0 ) {
            sortOrder = (int) Math.ceil( Math.random() * 100d );
        }
        if ( o.sortOrder == 0 ) {
            o.sortOrder = (int) Math.ceil( Math.random() * 100d );
        }
        return sortOrder - o.sortOrder;
    }
}
