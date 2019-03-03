package ru.rsmu.tempoLW.entities;

import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author leonid.
 */
@Entity
@Table(name = "sub_topic")
public class SubTopic implements Serializable {
    private static final long serialVersionUID = 1291396770268899101L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @Column
    private String title;

    @ManyToOne
    private ExamSubject subject;

    @Formula( "(select count(*) from question_info qi where qi.topic_id = id)" )
    private long questionsCount;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public ExamSubject getSubject() {
        return subject;
    }

    public void setSubject( ExamSubject subject ) {
        this.subject = subject;
    }

    public long getQuestionsCount() {
        return questionsCount;
    }

    public void setQuestionsCount( long questionsCount ) {
        this.questionsCount = questionsCount;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        SubTopic subTopic = (SubTopic) o;
        return id == subTopic.id &&
                Objects.equals( title, subTopic.title ) &&
                Objects.equals( subject, subTopic.subject );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, title, subject );
    }
}
