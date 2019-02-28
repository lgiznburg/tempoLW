package ru.rsmu.tempoLW.entities;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author leonid.
 */
@Entity
@Table(name="uploaded_images")
public class UploadedImage implements Serializable {
    private static final long serialVersionUID = 6440182350482769683L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] picture;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "source_name")
    private String sourceName;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture( byte[] picture ) {
        this.picture = picture;
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

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        UploadedImage that = (UploadedImage) o;
        return Objects.equals( id, that.id ) &&
                Objects.equals( contentType, that.contentType ) &&
                Objects.equals( sourceName, that.sourceName );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, contentType, sourceName );
    }
}
