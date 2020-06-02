package ru.rsmu.tempoLW.entities;

import org.apache.commons.lang.text.StrBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author leonid.
 */
@Entity
@Table(name = "testee")
public class Testee implements Serializable {
    private static final long serialVersionUID = 3700589677656860471L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @Column(unique = true)
    @NotNull
    private String login;

    @Column
    private String password;

    @Column(unique = true)
    @NotNull
    private String caseNumber;

    @Column
    private String firstName;

    @Column
    private String middleName;

    @Column
    private String lastName;

    @Column
    private String email;

    @Column(name = "expiration_date")
    @Temporal( TemporalType.DATE )
    private Date expirationDate;

    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin( String login ) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber( String caseNumber ) {
        this.caseNumber = caseNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName( String middleName ) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate( Date expirationDate ) {
        this.expirationDate = expirationDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        Testee testee = (Testee) o;
        return id == testee.id &&
                Objects.equals( login, testee.login ) &&
                Objects.equals( caseNumber, testee.caseNumber ) &&
                Objects.equals( firstName, testee.firstName ) &&
                Objects.equals( middleName, testee.middleName ) &&
                Objects.equals( lastName, testee.lastName );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, login, caseNumber, firstName, middleName, lastName );
    }

    @Transient
    public boolean isCredentialsExpired() {
        return expirationDate == null || expirationDate.before( new Date() );
    }

    @Transient
    public String getFullName() {
        StrBuilder builder = new StrBuilder( lastName );
        if ( StringUtils.isNotBlank( firstName ) ) {
            builder.append( " " ).append( firstName );
        }
        if ( StringUtils.isNotBlank( middleName ) ) {
            builder.append( " " ).append( middleName );
        }
        return builder.toString();
    }
}
