package ru.rsmu.tempoLW.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author leonid.
 */
@Entity
@Table(name="attached_files")
public class ResultAttachedFile implements Serializable {
    private static final long serialVersionUID = 6222122239076905391L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] content;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "source_name")
    private String sourceName;

    @ManyToOne
    @JoinColumn(name = "result_element_id")
    private ResultElement resultElement;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent( byte[] content ) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType( String contentType ) {
        this.contentType = contentType;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName( String sourceName ) {
        this.sourceName = sourceName;
    }

    public ResultElement getResultElement() {
        return resultElement;
    }

    public void setResultElement( ResultElement resultElement ) {
        this.resultElement = resultElement;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        ResultAttachedFile that = (ResultAttachedFile) o;
        return Objects.equals( id, that.id ) &&
                Objects.equals( contentType, that.contentType ) &&
                Objects.equals( sourceName, that.sourceName );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, contentType, sourceName );
    }
}
