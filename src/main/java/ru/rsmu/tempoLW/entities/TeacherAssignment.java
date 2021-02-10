package ru.rsmu.tempoLW.entities;

import ru.rsmu.tempoLW.entities.auth.User;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author leonid.
 */
@Entity
@Table(name = "teacher_assignments")
public class TeacherAssignment implements Serializable {
    private static final long serialVersionUID = 693695995002697411L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @ManyToOne
    @JoinColumn(name="result_id")
    private ExamResult examResult;

    @Column
    private boolean finished = false;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher( User teacher ) {
        this.teacher = teacher;
    }

    public ExamResult getExamResult() {
        return examResult;
    }

    public void setExamResult( ExamResult examResult ) {
        this.examResult = examResult;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished( boolean finished ) {
        this.finished = finished;
    }
}
