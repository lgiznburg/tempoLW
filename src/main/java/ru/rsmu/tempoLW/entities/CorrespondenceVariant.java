package ru.rsmu.tempoLW.entities;

import org.apache.tapestry5.beaneditor.Validate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author leonid.
 */
@Entity
@Table(name = "correspondence_variant")
public class CorrespondenceVariant implements Serializable, Comparable<CorrespondenceVariant> {
    private static final long serialVersionUID = -552500918103859107L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @Column
    @Validate( "required" )
    @Type( type = "org.hibernate.type.TextType" )
    private String text = "";

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable( name = "correspondence_answers",
            joinColumns = @JoinColumn(name = "correspondence_id"),
            inverseJoinColumns = @JoinColumn(name = "answer_id")
    )
    private List<AnswerVariant> correctAnswers = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "uploaded_image_id")
    private UploadedImage image;

    @ManyToOne
    private Question question;

    @Transient
    private int sortOrder;

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

    public List<AnswerVariant> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers( List<AnswerVariant> correctAnswers ) {
        this.correctAnswers = correctAnswers;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion( Question question ) {
        this.question = question;
    }

    public UploadedImage getImage() {
        return image;
    }

    public void setImage( UploadedImage image ) {
        this.image = image;
    }

    @Override
    public int compareTo( CorrespondenceVariant o ) {
        if ( sortOrder == 0 ) {
            sortOrder = (int) Math.ceil( Math.random() * 100d );
        }
        if ( o.sortOrder == 0 ) {
            o.sortOrder = (int) Math.ceil( Math.random() * 100d );
        }
        return sortOrder - o.sortOrder;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        CorrespondenceVariant that = (CorrespondenceVariant) o;
        return id == that.id &&
                Objects.equals( text, that.text );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, text );
    }
}
