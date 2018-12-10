package ru.rsmu.tempoLW.entities.auth;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

/**
 * @author leonid.
 */
@Entity
@Table(name = "user")
public class User {
    private static final long serialVersionUID = 5682601783979092294L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @Column
    @Email
    @Index( name = "username_index")
    private String username;

    @Column
    private String password;

    @Column
    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable( name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    @Fetch(FetchMode.SELECT )
    @Cascade(CascadeType.ALL )
    private List<UserRole> roles;


    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles( List<UserRole> roles ) {
        this.roles = roles;
    }
}
