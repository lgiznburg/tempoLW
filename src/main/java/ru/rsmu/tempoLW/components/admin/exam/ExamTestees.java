package ru.rsmu.tempoLW.components.admin.exam;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.Testee;

import java.util.*;

/**
 * @author leonid.
 */
public class ExamTestees {
    @Parameter(required = true)
    @Property
    private Long examId;

    @Property
    private ExamSchedule exam;

    @Property
    private Testee testee;

    @Property
    private Boolean rtf;

    private Map<Long,ExamResult> resultsMap;

    @Inject
    private ExamDao examDao;

    public void setupRender() {
        rtf = true;
        resultsMap = new HashMap<>();
        exam = examId != null ? examDao.find( ExamSchedule.class, examId ) : null;
        if ( exam != null && exam.getTestees() != null ) {
            List<ExamResult> results = examDao.findExamResults( exam );
            if ( results != null ) {
                results.forEach( result -> resultsMap.put( result.getTestee().getId(), result ) );
            }
            exam.getTestees().sort( new Comparator<Testee>() {
                @Override
                public int compare( Testee o1, Testee o2 ) {
                    return o1.getLastName().compareTo( o2.getLastName() );
                }
            } );
        }
    }

    public ExamResult getCurrentExamResult() {
        if ( resultsMap != null ) {
            return resultsMap.get( testee.getId() );
        }
        return null;
    }

}
