package ru.rsmu.tempoLW.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author leonid.
 */
@Entity
@Table(name = "exam_to_testee")
public class ExamToTestee implements Serializable {
    private static final long serialVersionUID = -2720477453548463443L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @ManyToOne
    @JoinColumn(name = "testee_id")
    private Testee testee;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private ExamSchedule exam;

    @Column
    private String password;

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

    public ExamSchedule getExam() {
        return exam;
    }

    public void setExam( ExamSchedule exam ) {
        this.exam = exam;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }
}
