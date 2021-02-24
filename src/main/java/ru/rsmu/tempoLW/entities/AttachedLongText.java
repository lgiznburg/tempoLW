package ru.rsmu.tempoLW.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author leonid.
 */
@Entity
@Table(name = "attached_text")
public class AttachedLongText implements Serializable {
    private static final long serialVersionUID = -5936112241669648613L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String text;

    @OneToOne
    @JoinColumn(name = "result_element_id")
    private ResultElement resultElement;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
    }

    public ResultElement getResultElement() {
        return resultElement;
    }

    public void setResultElement( ResultElement resultElement ) {
        this.resultElement = resultElement;
    }
}
