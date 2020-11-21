package ru.rsmu.tempoLW.rest;

import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.ExamToTestee;
import ru.rsmu.tempoLW.entities.Testee;
import ru.rsmu.tempoLW.rest.examsapimodel.ExamFacade;
import ru.rsmu.tempoLW.rest.examsapimodel.PersonInfo;
import ru.rsmu.tempoLW.rest.examsapimodel.PersonResult;
import ru.rsmu.tempoLW.services.TesteeCredentialsService;
import ru.rsmu.tempoLW.utils.TesteeLoader;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
@Path( "/exam" )
@RolesAllowed( {"admin", "subject_admin"} )
public class ExamsApi {

    @Inject
    private ExamDao examDao;

    @Inject
    private TesteeCredentialsService testeeCredentialsService;

    /**
     * return all exams of this year
     */
    @GET
    @Path( "/list" )
    @Produces( "application/json" )
    public List<ExamFacade> getExamsList() {
        List<ExamSchedule> exams = examDao.findExams( new Date() );
        return exams.stream().map( ExamFacade::new ).collect( Collectors.toList() );
    }

    /**
     * return upcoming exams
     */
    @GET
    @Path( "/list/upcoming" )
    @Produces( "application/json" )
    public List<ExamFacade> getUpcomingExamsList() {
        List<ExamSchedule> exams = examDao.findUpcomingExams();
        return exams.stream().map( ExamFacade::new ).collect( Collectors.toList() );
    }

    /**
     * return The exam
     */
    @GET
    @Path( "/{id}" )
    @Produces( "application/json" )
    public ExamFacade getExam( @PathParam ( "id" ) Long id ) {
        ExamSchedule exam = examDao.find( ExamSchedule.class, id );
        if ( exam != null ) {
            return new ExamFacade( exam );
        }
        return null;
    }

    @POST
    @Path( "/{id}/participants" )
    @Produces( "application/json" )
    public Response uploadParticipants( @PathParam( "id" ) Long id,
                                        List<PersonInfo> personInfoList ) {
        ExamSchedule exam = examDao.find( ExamSchedule.class, id );
        TesteeLoader testeeLoader = new TesteeLoader( null );

        if ( exam != null ) {
            List<Testee> testees = new ArrayList<>();

            for ( PersonInfo personInfo : personInfoList ) {
                Testee testee = new Testee();
                testee.setCaseNumber( personInfo.getCaseNumber() );
                testee.setEmail( personInfo.getEmail() );
                testee.setFirstName( personInfo.getFirstName() );
                testee.setMiddleName( personInfo.getMiddleName() );
                testee.setLastName( personInfo.getLastName() );
                testee.setLogin( testeeLoader.createLogin( testee.getCaseNumber() ) );
                examDao.save( testee );

                testees.add( testee );
            }
            exam.setTestees( testees );
            examDao.save( exam );

            testeeCredentialsService.createPasswordsAndEmails( exam );
        }
        return Response.ok().build();
    }


    @GET
    @Path( "/{id}/results" )
    @Produces( "application/json" )
    public List<PersonResult> getExamResults( @PathParam( "id" ) Long id ) {
        ExamSchedule exam = examDao.find( ExamSchedule.class, id );
        List<PersonResult> results = new ArrayList<>();
        if ( exam != null ) {
            List<ExamResult> examResults = examDao.findExamResults( exam );

            Map<Testee,ExamResult> testeeToResult = new HashMap<>();
            examResults.forEach( er -> testeeToResult.put( er.getTestee(), er ) );

            for ( ExamToTestee examToTestee : exam.getExamToTestees() ) {
                PersonResult personResult = new PersonResult();
                personResult.setCaseNumber( examToTestee.getTestee().getCaseNumber() );
                ExamResult examResult = testeeToResult.get( examToTestee.getTestee() );
                if ( examResult == null ) {
                    personResult.setStatus( "неявка" );
                    personResult.setFinalScore( 0 );
                }
                else {
                    if ( examResult.isFinished() ) {
                        personResult.setFinalScore( examResult.getMarkTotal() );
                        if ( examResult.getMarkTotal() == 0 ) {
                            personResult.setStatus( "ожидает проверки" );
                        }
                        else {
                            personResult.setStatus( "завершено" );
                        }
                    }
                    else {
                        personResult.setFinalScore( 0 );
                        personResult.setStatus( "не завершено" );
                    }
                }
                results.add( personResult );
            }
        }
        return results;
    }
}
