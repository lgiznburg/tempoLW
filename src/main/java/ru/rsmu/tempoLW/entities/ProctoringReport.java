package ru.rsmu.tempoLW.entities;

import org.codehaus.jackson.map.ObjectMapper;
import ru.rsmu.tempoLW.rest.ProctoringApiReport;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * @author leonid.
 */
@Entity
@Table(name = "proctoring_reports")
public class ProctoringReport implements Serializable {
    private static final long serialVersionUID = 1026576411210817399L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    @Column(name = "session_id", unique = true)
    private String sessionId;

    /**
     * Session status
     */
    @Column
    private String status;

    /**
     * Session duration in minutes
     */
    @Column
    private int duration;

    /**
     * Session start time
     */
    @Column(name = "started_at")
    @Temporal( TemporalType.TIMESTAMP )
    private Date startedAt;
    /**
     * Session end time
     */
    @Column(name = "stopped_at")
    @Temporal( TemporalType.TIMESTAMP )
    private Date stoppedAt;

    /**
     * Average score
     */
    @Column
    private int score;

    /**
     * All metrics in JSON string
     * {[metric:value]}
     */
    @Column
    private String metrics;

    @Column
    private String proctor;

    @Column
    private String comment;

    @Column
    private String link;

    @ManyToOne
    @JoinColumn(name = "exam_result_id")
    private ExamResult examResult;

    public ProctoringReport() {
    }

    public ProctoringReport( ProctoringApiReport apiReport ) {
        sessionId = apiReport.getId();
        update( apiReport );
    }

    public void update( ProctoringApiReport apiReport ) {
        status = apiReport.getStatus();
        duration = apiReport.getDuration();
        startedAt = apiReport.getStartedAt();
        stoppedAt = apiReport.getStoppedAt();
        score = apiReport.getScore();
        proctor = apiReport.getProctor();
        comment = apiReport.getComment();
        link = apiReport.getLink();
        if ( apiReport.getAverages() != null ) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                metrics = mapper.writeValueAsString( apiReport.getAverages() );
            } catch (IOException e) {
                // do nothing
            }
        }
    }


    public long getId() {
        return id;
    }

    public void setId( long id ) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId( String sessionId ) {
        this.sessionId = sessionId;
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

    public String getMetrics() {
        return metrics;
    }

    public void setMetrics( String metrics ) {
        this.metrics = metrics;
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

    public ExamResult getExamResult() {
        return examResult;
    }

    public void setExamResult( ExamResult examResult ) {
        this.examResult = examResult;
    }
}
