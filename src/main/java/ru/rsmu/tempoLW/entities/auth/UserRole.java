package ru.rsmu.tempoLW.entities.auth;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author leonid.
 */
@Entity
@Table(name = "roles")
public class UserRole implements Serializable {
    private static final long serialVersionUID = 4075668837954381431L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRoleName roleName;

    public String getAuthority() {
        return roleName.name();
    }

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public UserRoleName getRoleName() {
        return roleName;
    }

    public void setRoleName( UserRoleName roleName ) {
        this.roleName = roleName;
    }
}
