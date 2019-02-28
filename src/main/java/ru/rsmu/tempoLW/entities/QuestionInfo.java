package ru.rsmu.tempoLW.entities;

import org.apache.tapestry5.beaneditor.Validate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author leonid.
 */

@Entity
@Table(name = "question_info")
public class QuestionInfo implements Serializable {
    private static final long serialVersionUID = -5544204962124341458L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @Column
    @Validate( "required" )
    private String name;

    @Column
    private int code;

    @Column(name = "max_score")
    private int maxScore;

    @Column
    private int complexity;

    @ManyToOne
    private ExamSubject subject;

    @ManyToOne
    private SubTopic topic;

    @OneToMany(mappedBy = "questionInfo")
    private List<Question> questions;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode( int code ) {
        this.code = code;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore( int maxScore ) {
        this.maxScore = maxScore;
    }

    public int getComplexity() {
        return complexity;
    }

    public void setComplexity( int complexity ) {
        this.complexity = complexity;
    }

    public ExamSubject getSubject() {
        return subject;
    }

    public void setSubject( ExamSubject subject ) {
        this.subject = subject;
    }

    public SubTopic getTopic() {
        return topic;
    }

    public void setTopic( SubTopic topic ) {
        this.topic = topic;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions( List<Question> questions ) {
        this.questions = questions;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        QuestionInfo that = (QuestionInfo) o;
        return id == that.id &&
                code == that.code &&
                maxScore == that.maxScore &&
                complexity == that.complexity &&
                Objects.equals( name, that.name ) &&
                Objects.equals( topic, that.topic );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, name, code, maxScore, complexity, topic );
    }
}
