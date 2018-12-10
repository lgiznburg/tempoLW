package ru.rsmu.tempoLW.entities;

import org.apache.tapestry5.beaneditor.Validate;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author leonid.
 */
@Entity
@Table(name = "test_subject")
public class TestSubject implements Serializable {
    private static final long serialVersionUID = -3126994204387882375L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @Column
    @Validate( "required" )
    private String title = "";

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
}
