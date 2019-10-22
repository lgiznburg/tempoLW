package ru.rsmu.tempoLW.entities;

import org.apache.tapestry5.beaneditor.Validate;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author leonid.
 */
@Entity
@Table(name = "answer_variant")
public class AnswerVariant implements Serializable, Comparable<AnswerVariant> {
    private static final long serialVersionUID = 5012066124661313001L;

    private static final Pattern USE_REGEX = Pattern.compile( "^%(.*)%(=(.*))?$" );

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @Column
    @Validate( "required" )
    @Type( type = "org.hibernate.type.TextType" )
    private String text = "";

    @Column
    private boolean correct = false;

    @Column(name = "sequence_order")
    @ColumnDefault( "0" )
    private int sequenceOrder = 0;

    @ManyToOne
    private Question question;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "uploaded_image_id")
    private UploadedImage image;

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

    public int getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder( int sequenceOrder ) {
        this.sequenceOrder = sequenceOrder;
    }

    public UploadedImage getImage() {
        return image;
    }

    public void setImage( UploadedImage image ) {
        this.image = image;
    }

    @Transient
    public String getRegex() {
        Matcher matcher = USE_REGEX.matcher( text );
        if ( matcher.matches() ) {
            return matcher.group(1);
        }
        return null;
    }

    @Transient
    public String getReadableText() {
        Matcher matcher = USE_REGEX.matcher( text );
        if ( matcher.matches() ) {
            return matcher.group(3);
        }
        if ( text.contains( "|" ) ) {
            return text.replace( "|", " ИЛИ " );
        }
        return text;
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

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        AnswerVariant that = (AnswerVariant) o;
        return id == that.id &&
                correct == that.correct &&
                sequenceOrder == that.sequenceOrder &&
                Objects.equals( text, that.text );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, text, correct, sequenceOrder );
    }
}
