package ru.rsmu.tempoLW.rest.examsapimodel;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author leonid.
 */
@XmlRootElement( name = "result" )
public class PersonResult {
    private String caseNumber;
    private String status;
    private int finalScore;

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber( String caseNumber ) {
        this.caseNumber = caseNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus( String status ) {
        this.status = status;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public void setFinalScore( int finalScore ) {
        this.finalScore = finalScore;
    }
}
