package ru.rsmu.tempoLW.entities.auth;

import ru.rsmu.tempoLW.entities.ExamSubject;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author leonid.
 */
@Entity
@Table(name = "subject_admins")
public class SubjectManager implements Serializable {
    private static final long serialVersionUID = 2108155555682578279L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(name = "manager_subjects",
            joinColumns = @JoinColumn(name = "manager_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private List<ExamSubject> subjects;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser( User user ) {
        this.user = user;
    }

    public List<ExamSubject> getSubjects() {
        return subjects;
    }

    public void setSubjects( List<ExamSubject> subjects ) {
        this.subjects = subjects;
    }
}
