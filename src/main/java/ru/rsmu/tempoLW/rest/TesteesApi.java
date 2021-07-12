package ru.rsmu.tempoLW.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.TesteeDao;
import ru.rsmu.tempoLW.entities.ExamToTestee;
import ru.rsmu.tempoLW.entities.Testee;
import ru.rsmu.tempoLW.rest.examsapimodel.TesteeExamInfo;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author leonid.
 */
@Path( "/testee" )
@RolesAllowed( {"admin", "api"} )
public class TesteesApi {

    @Inject
    private TesteeDao testeeDao;

    @Inject
    private ExamDao examDao;

    @GET
    @Path( "/{caseNumber}/exams" )
    @Produces("application/json")
    public Map<String, Object> getExamsForTestee( @PathParam ( "caseNumber" ) String caseNumber ) {
        Map<String,Object> result = new HashMap<>();
        Testee testee = testeeDao.findByCaseNumber( caseNumber );
        if ( testee != null ) {
            List<ExamToTestee> examToTestees = examDao.findExamsForTestee( testee );
            ObjectMapper mapper = new ObjectMapper();
            try {
                String tokenJson = mapper.writeValueAsString( examToTestees.stream().map( TesteeExamInfo::new ).collect( Collectors.toList()) );
                result.put( "token", Base64.getEncoder().encodeToString( tokenJson.getBytes( StandardCharsets.UTF_8 ) ) ) ;
            } catch (JsonProcessingException e) {
                //
            }
            return result;
        }
        result.put( "token", Base64.getEncoder().encodeToString( "[]".getBytes( StandardCharsets.UTF_8 ) ) );
        return result;
    }
}
