package ru.rsmu.tempoLW.entities;

import org.apache.tapestry5.beaneditor.Validate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

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
    private TestSubject subject;

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

    public TestSubject getSubject() {
        return subject;
    }

    public void setSubject( TestSubject subject ) {
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
}
