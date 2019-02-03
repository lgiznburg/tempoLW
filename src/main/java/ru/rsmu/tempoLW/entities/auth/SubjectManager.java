package ru.rsmu.tempoLW.entities.auth;

import ru.rsmu.tempoLW.entities.TestSubject;

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

    @OneToMany
    @JoinTable(name = "manager_subjects",
            joinColumns = @JoinColumn(name = "subject_id"),
            inverseJoinColumns = @JoinColumn(name = "manager_id")
    )
    private List<TestSubject> subjects;

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

    public List<TestSubject> getSubjects() {
        return subjects;
    }

    public void setSubjects( List<TestSubject> subjects ) {
        this.subjects = subjects;
    }
}
