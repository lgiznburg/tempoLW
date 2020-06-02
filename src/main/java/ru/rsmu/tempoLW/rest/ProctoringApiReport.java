package ru.rsmu.tempoLW.rest;

/**
 * @author leonid.
 */

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Map;

@XmlRootElement( name = "report" )
public class ProctoringApiReport {

    /**
     * Session ID
     */
    private String id;
    /**
     * Session status
     */
    private String status;

    /**
     * Session duration in minutes
     */
    private int duration;

    /**
     * Session start time
     */
    private Date startedAt;
    /**
     * Session end time
     */
    private Date stoppedAt;

    /**
     * Average score
     */
    private int score;

    /**
     * All metrics
     */
    private Map<String, String> averages;

    /**
     * Student login
     */
    private String student;

    /**
     * Proctor login
     */
    private String proctor;

    /**
     * Proctor's comment
     */
    private String comment;

    /**
     * Link to report on proctoring server
     */
    private String link;

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus( String status ) {
        this.status = status;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration( int duration ) {
        this.duration = duration;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt( Date startedAt ) {
        this.startedAt = startedAt;
    }

    public Date getStoppedAt() {
        return stoppedAt;
    }

    public void setStoppedAt( Date stoppedAt ) {
        this.stoppedAt = stoppedAt;
    }

    public int getScore() {
        return score;
    }

    public void setScore( int score ) {
        this.score = score;
    }

    public Map<String, String> getAverages() {
        return averages;
    }

    public void setAverages( Map<String, String> averages ) {
        this.averages = averages;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent( String student ) {
        this.student = student;
    }

    public String getProctor() {
        return proctor;
    }

    public void setProctor( String proctor ) {
        this.proctor = proctor;
    }

    public String getComment() {
        return comment;
    }

    public void setComment( String comment ) {
        this.comment = comment;
    }

    public String getLink() {
        return link;
    }

    public void setLink( String link ) {
        this.link = link;
    }
}
