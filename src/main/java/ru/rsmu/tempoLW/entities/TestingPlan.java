package ru.rsmu.tempoLW.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author leonid.
 */
@Entity
@Table(name = "testing_plan")
public class TestingPlan implements Serializable {
    private static final long serialVersionUID = -1041652542973452793L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @ManyToOne
    private ExamSubject subject;

    @OneToMany(mappedBy = "testingPlan", cascade = CascadeType.ALL )
    private List<TestingPlanRule> rules;

    @Column
    private String name;

    @Column
    private boolean enabled;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public ExamSubject getSubject() {
        return subject;
    }

    public void setSubject( ExamSubject subject ) {
        this.subject = subject;
    }

    public List<TestingPlanRule> getRules() {
        return rules;
    }

    public void setRules( List<TestingPlanRule> rules ) {
        this.rules = rules;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }
}
