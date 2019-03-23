package ru.rsmu.tempoLW.entities;

import org.apache.tapestry5.beaneditor.Validate;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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
    @Validate( "required" )
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

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        TestingPlan that = (TestingPlan) o;
        return id == that.id &&
                enabled == that.enabled &&
                Objects.equals( subject, that.subject ) &&
                Objects.equals( name, that.name );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, subject, name, enabled );
    }
}
