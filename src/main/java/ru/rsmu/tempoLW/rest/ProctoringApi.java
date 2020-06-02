package ru.rsmu.tempoLW.rest;

import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.dao.TesteeDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.ProctoringReport;
import ru.rsmu.tempoLW.entities.Testee;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * @author leonid.
 */
@Path( "/proctoring/result" )
public class ProctoringApi {

    @Inject
    private ExamDao examDao;

    @Inject
    private TesteeDao testeeDao;


    @POST
    @Produces({"application/json"})
    public Response receiveProctoringResult( ProctoringApiReport apiReport ) {
        ProctoringReport report = examDao.findProctoringReport( apiReport.getId() );
        if ( report == null ) {
            // create new report
            report = new ProctoringReport( apiReport );
            // find and set examResult
            String[] parts = apiReport.getId().split( "\\." );
            if ( parts.length >= 3 ) { // correct size == 3
                String testeeCaseNumber = parts[1];
                Long examId = Long.getLong( parts[2] );

                Testee testee = testeeDao.findByCaseNumber( testeeCaseNumber );
                ExamSchedule exam = examDao.find( ExamSchedule.class, examId );
                if ( testee != null && exam != null ) {
                    ExamResult examResult = examDao.findExamResultForTestee( exam, testee );
                    report.setExamResult( examResult );
                }
            }
        }
        else {
            report.update( apiReport );
        }
        if ( report.getExamResult() != null ) {
            examDao.save( report );
        }
        return Response.ok().build();
    }
}
