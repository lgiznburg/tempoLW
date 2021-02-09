package ru.rsmu.tempoLW.datasource;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.auth.User;

import java.util.List;

/**
 * @author leonid.
 */
public class ExamResultsDataSource implements GridDataSource {

    private final ExamDao examDao;

    private final User teacher;

    private List<ExamResult> examResults;

    private int startIndex;

    public ExamResultsDataSource( ExamDao examDao, User teacher ) {
        this.examDao = examDao;
        this.teacher = teacher;
    }

    @Override
    public int getAvailableRows() {
        return examDao.countAssignedResults( teacher );
    }

    @Override
    public void prepare( int startIndex, int endIndex, List<SortConstraint> list ) {
        this.startIndex = startIndex;
        examResults = examDao.findAssignedResults( teacher, startIndex, endIndex-startIndex+1 );
    }

    @Override
    public Object getRowValue( int index ) {
        return examResults.get( index - startIndex );
    }

    @Override
    public Class getRowType() {
        return ExamResult.class;
    }
}
