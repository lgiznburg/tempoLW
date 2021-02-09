package ru.rsmu.tempoLW.entities;

import ru.rsmu.tempoLW.services.EmailType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author leonid.
 */
@Entity
@Table(name = "email_queue")
public class EmailQueue implements Serializable {
    private static final long serialVersionUID = -55470194479876089L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @ManyToOne
    @JoinColumn(name = "testee_id")
    private Testee testee;

    @Column(name = "email_type")
    @Enumerated(value = EnumType.STRING)
    private EmailType emailType;

    @Column(name = "model")
    private String model;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private EmailQueueStatus status;

    @Column(name = "created_at")
    @Temporal( value = TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "updated_at")
    @Temporal( value = TemporalType.TIMESTAMP)
    private Date updatedDate;

    public EmailQueue() {
        status = EmailQueueStatus.NEW;
        createdDate = new Date();
        updatedDate = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public Testee getTestee() {
        return testee;
    }

    public void setTestee( Testee testee ) {
        this.testee = testee;
    }

    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType( EmailType emailType ) {
        this.emailType = emailType;
    }

    public String getModel() {
        return model;
    }

    public void setModel( String model ) {
        this.model = model;
    }

    public EmailQueueStatus getStatus() {
        return status;
    }

    public void setStatus( EmailQueueStatus status ) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate( Date createdDate ) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate( Date updatedDate ) {
        this.updatedDate = updatedDate;
    }
}
