package ru.rsmu.tempoLW.entities;

import org.hibernate.annotations.Type;
import ru.rsmu.tempoLW.data.QuestionType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author leonid.
 */
@Entity
@Table(name = "question")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn( name = "type", discriminatorType = DiscriminatorType.STRING )
public abstract class Question implements Serializable {
    private static final long serialVersionUID = -107174030301141499L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @Column(insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_info_id")
    private QuestionInfo questionInfo;

    @Column
    private int version;

    @Column
    @Type( type = "org.hibernate.type.TextType" )
    private String text;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "uploaded_image_id")
    private UploadedImage image;

    @Column(name = "created_date")
    @Temporal( TemporalType.DATE )
    private Date createdDate;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType( QuestionType type ) {
        this.type = type;
    }
    public QuestionInfo getQuestionInfo() {
        return questionInfo;
    }

    public void setQuestionInfo( QuestionInfo questionInfo ) {
        this.questionInfo = questionInfo;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion( int version ) {
        this.version = version;
    }

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
    }

    public UploadedImage getImage() {
        return image;
    }

    public void setImage( UploadedImage image ) {
        this.image = image;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate( Date createdDate ) {
        this.createdDate = createdDate;
    }

    abstract public int countErrors( List<ResultElement> elements );

    @Transient
    public boolean isManualCheckingRequired() { return false; }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !(o instanceof Question) ) return false;
        Question question = (Question) o;
        return id == question.id &&
                type == question.type &&
                questionInfo.equals( question.questionInfo ) &&
                Objects.equals( text, question.text );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, type, questionInfo, text );
    }
}
