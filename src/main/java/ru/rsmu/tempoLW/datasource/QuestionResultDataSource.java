package ru.rsmu.tempoLW.datasource;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import ru.rsmu.tempoLW.entities.QuestionResult;

import java.util.Comparator;
import java.util.List;

/**
 * @author leonid.
 */
public class QuestionResultDataSource implements GridDataSource {
    private List<QuestionResult> results;

    public QuestionResultDataSource( List<QuestionResult> results ) {
        this.results = results;
    }

    @Override
    public int getAvailableRows() {
        return results.size();
    }

    @Override
    public void prepare( int startIndex, int endIndex, List<SortConstraint> sortConstraints ) {
        results.sort( Comparator.comparingInt( QuestionResult::getOrderNumber ) );
    }

    @Override
    public Object getRowValue( int index ) {
        return results.get( index );
    }

    @Override
    public Class<?> getRowType() {
        return QuestionResult.class;
    }
}
