package ru.rsmu.tempoLW.rest.examsapimodel;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author leonid.
 */
@XmlRootElement( name = "personInfo" )
public class PersonInfo {
    private String caseNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;

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

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }
}
