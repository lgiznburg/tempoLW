package ru.rsmu.tempoLW.rest.examsapimodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.ExamToTestee;

import java.util.Date;

/**
 * @author leonid.
 */
public class TesteeExamInfo {

    @JsonIgnore
    private String caseNumber;
    private String examName;
    private String testingPlanName;
    private Date examStartTime;
    private Date examEndTime;
    private String duration;
    private String token;
    //private boolean allowedToLogin;
    private Long examId;

    public TesteeExamInfo( ExamToTestee examToTestee ) {
        caseNumber = examToTestee.getTestee().getCaseNumber();
        ExamSchedule exam = examToTestee.getExam();
        examName = exam.getName();
        testingPlanName = exam.getTestingPlan().getName();
        examStartTime = exam.getPeriodStartTime();
        examEndTime = exam.getPeriodEndTime();
        duration = String.format("%d:%02d", exam.getDurationHours(), exam.getDurationMinutes() );
        Date currentTime = new Date();
        //allowedToLogin = exam.getPeriodStartTime().before( currentTime ) && exam.getPeriodEndTime().after( currentTime );
        if ( StringUtils.isNotBlank( examToTestee.getPassword() ) ) {
            token = examToTestee.getTestee().getLogin() + ":" + examToTestee.getPassword(); // Base64.getEncoder().encodeToString( logopass.getBytes( StandardCharsets.UTF_8 ) );
        }
        else {
            token = "";
        }
        examId = examToTestee.getExam().getId();
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber( String caseNumber ) {
        this.caseNumber = caseNumber;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName( String examName ) {
        this.examName = examName;
    }

    public String getTestingPlanName() {
        return testingPlanName;
    }

    public void setTestingPlanName( String testingPlanName ) {
        this.testingPlanName = testingPlanName;
    }

    public Date getExamStartTime() {
        return examStartTime;
    }

    public void setExamStartTime( Date examStartTime ) {
        this.examStartTime = examStartTime;
    }

    public Date getExamEndTime() {
        return examEndTime;
    }

    public void setExamEndTime( Date examEndTime ) {
        this.examEndTime = examEndTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration( String duration ) {
        this.duration = duration;
    }

/*
    public boolean isAllowedToLogin() {
        return allowedToLogin;
    }

    public void setAllowedToLogin( boolean allowedToLogin ) {
        this.allowedToLogin = allowedToLogin;
    }
*/

    public String getToken() {
        return token;
    }

    public void setToken( String token ) {
        this.token = token;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId( Long examId ) {
        this.examId = examId;
    }
}
