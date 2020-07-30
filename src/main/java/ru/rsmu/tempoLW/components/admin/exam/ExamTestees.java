package ru.rsmu.tempoLW.components.admin.exam;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import ru.rsmu.tempoLW.consumabales.proctoring.MetricInfo;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.entities.*;

import java.io.IOException;
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

    /**
     * Testees results for the exam
     */
    private Map<Long,ExamResult> resultsMap;

    /**
     * Proctoring results for the exam. Map by testee ID
     */
    private Map<Long, ProctoringReport> proctoringReportMap;

    private Map<Long, List<MetricInfo>> proctoringMetrics;

    @Property
    private MetricInfo metric;

    @Inject
    private ExamDao examDao;

    public void setupRender() {
        rtf = true;
        resultsMap = new HashMap<>();
        proctoringReportMap = new HashMap<>();
        proctoringMetrics = new HashMap<>();
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

            // Proctoring reports
            if ( exam.isUseProctoring() ) {
                List<ProctoringReport> reports = examDao.findProctoringForExam( exam );
                if ( reports != null ) {
                    reports.forEach( rep -> proctoringReportMap.put( rep.getExamResult().getTestee().getId(), rep ) );
                    reports.forEach( rep -> proctoringMetrics.put( rep.getExamResult().getTestee().getId(), getMetricInfos( rep.getMetrics() ) ) );
                }
            }
        }
    }

    private List<MetricInfo> getMetricInfos( String jsonString ) {
        List<MetricInfo> metricInfos = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String,String> parsed = mapper.readValue(jsonString, new TypeReference<Map<String,String>>(){} );
            parsed.forEach( (code, value) -> {
                MetricInfo info = new MetricInfo( code, Integer.parseInt( value ) );
                if ( info.getValue() > 0 ) {
                    metricInfos.add( info );
                }
            } );
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return metricInfos;
    }

    public ExamResult getCurrentExamResult() {
        if ( resultsMap != null ) {
            return resultsMap.get( testee.getId() );
        }
        return null;
    }

    public String getTesteeFullName() {
        StringBuilder builder = new StringBuilder( testee.getLastName() );
        if ( StringUtils.isNotBlank( testee.getFirstName() ) ) {
            builder.append( testee.getFirstName() );
        }
        if ( StringUtils.isNotBlank( testee.getMiddleName() ) ) {
            builder.append( testee.getMiddleName() );
        }
        return builder.toString();
    }

    public ProctoringReport getProctoringReport() {
        return proctoringReportMap.get( testee.getId() );
    }

    public List<MetricInfo> getProctoringMetrics() {
        return proctoringMetrics.get( testee.getId() );
    }

    public boolean getProctoringExist() {
        return exam.isUseProctoring() && proctoringReportMap.containsKey( testee.getId() );
    }
}
