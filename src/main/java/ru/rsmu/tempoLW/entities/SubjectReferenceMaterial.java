package ru.rsmu.tempoLW.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author leonid.
 */
@Entity
@Table(name = "subject_reference_materials")
public class SubjectReferenceMaterial implements Serializable {
    private static final long serialVersionUID = -4825349448092503882L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private ExamSubject subject;

    @ManyToOne( cascade = CascadeType.REMOVE )
    @JoinColumn(name = "image_id")
    private UploadedImage image;

    @Column(name = "title")
    private String title;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public ExamSubject getSubject() {
        return subject;
    }

    public void setSubject( ExamSubject subject ) {
        this.subject = subject;
    }

    public UploadedImage getImage() {
        return image;
    }

    public void setImage( UploadedImage image ) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }
}
