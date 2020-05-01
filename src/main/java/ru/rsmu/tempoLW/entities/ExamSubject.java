package ru.rsmu.tempoLW.entities;

import org.apache.tapestry5.beaneditor.Validate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author leonid.
 */
@Entity
@Table(name = "test_subject")
public class ExamSubject implements Serializable {
    private static final long serialVersionUID = -3126994204387882375L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @Column
    @Validate( "required" )
    private String title = "";

    @Column
    @Validate( "required" )
    private String locale;

    /**
     * Show number of correct answers
     */
    @Column(name = "show_answers_quantity")
    private boolean showAnswersQuantity;

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

    public String getLocale() {
        return locale;
    }

    public void setLocale( String locale ) {
        this.locale = locale;
    }

    public boolean isShowAnswersQuantity() {
        return showAnswersQuantity;
    }

    public void setShowAnswersQuantity( boolean showAnswersQuantity ) {
        this.showAnswersQuantity = showAnswersQuantity;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        ExamSubject that = (ExamSubject) o;
        return id == that.id &&
                Objects.equals( title, that.title ) &&
                Objects.equals( locale, that.locale );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, title, locale );
    }
}
