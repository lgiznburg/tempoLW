package ru.rsmu.tempoLW.components.admin.exam;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.ExamToTestee;
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

    @Property
    private List<Testee> testees;

    private Map<Long,ExamResult> resultsMap;

    @Inject
    private ExamDao examDao;

    public void setupRender() {
        rtf = true;
        resultsMap = new HashMap<>();
        testees = new ArrayList<>();
        exam = examId != null ? examDao.find( ExamSchedule.class, examId ) : null;
        if ( exam != null && exam.getExamToTestees() != null ) {
            List<ExamResult> results = examDao.findExamResults( exam );
            if ( results != null ) {
                results.forEach( result -> resultsMap.put( result.getTestee().getId(), result ) );
            }
            exam.getExamToTestees().sort( new Comparator<ExamToTestee>() {
                @Override
                public int compare( ExamToTestee o1, ExamToTestee o2 ) {
                    return o1.getTestee().getLastName().compareTo( o2.getTestee().getLastName() );
                }
            } );
            exam.getExamToTestees().forEach( examToTestee -> testees.add( examToTestee.getTestee() ) );
        }
    }

    public ExamResult getCurrentExamResult() {
        if ( resultsMap != null ) {
            return resultsMap.get( testee.getId() );
        }
        return null;
    }

}
