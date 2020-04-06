package ru.rsmu.tempoLW.pages.admin;

import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import ru.rsmu.tempoLW.consumabales.CrudMode;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.datasource.ExamResultDataSource;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.QuestionResult;
import ru.rsmu.tempoLW.entities.Testee;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

// ann
@Import( stylesheet = {"katex/katex.css"})
public class Appeal {
    @PageActivationContext(index = 0)
    @Property
    private ExamSchedule exam;

    @PageActivationContext(index = 1)
    @Property
    private Testee testee;

    @Inject
    private ExamDao examDao;

    @Property
    private ExamResult examResult;

    // for interaction through question result list
    @Property
    private QuestionResult questionResult;

    @PageActivationContext(index = 2)
    @Property
    private QuestionResult resultForView;

    @Environmental
    private JavaScriptSupport javaScriptSupport;

    @Inject
    private Request request;

    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;

    @InjectComponent
    private Zone questionListZone;

    @InjectComponent
    private Zone resultViewZone;

    public int getQuestionMax(){
        return questionResult.getQuestion().getQuestionInfo().getMaxScore()*questionResult.getScoreCost();
    }

    public Map<String, Object> getQueryParams() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("examId", exam.getId());
        queryParams.put("mode", CrudMode.VIEW_ELEMENTS);
        return queryParams;
    }

    public Map<String, Object> getQustionParams() {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("questionId", questionResult.getId());
        return queryParams;
    }

    public void onActivate() {
        examResult = examDao.findExamResultForTestee(exam,testee);
        examResult.getQuestionResults().sort( Comparator.comparingInt( QuestionResult::getOrderNumber ) );
        if (resultForView == null){
            resultForView = examResult.getQuestionResults().get(0);
        }
    }

    public void setupRender() {
        javaScriptSupport.require( "katex/katex" );
        javaScriptSupport.require( "katex/contrib/auto-render" ).invoke( "renderMathInElementOfClass" ).with( "container" );
    }

    public GridDataSource getResultsDataSource() {
        return new ExamResultDataSource( examResult.getQuestionResults() );
    }

    public boolean isActiveRow() {
        return questionResult.getId() == resultForView.getId();
    }

    public void onQuestionSelect( QuestionResult result ) {
        if ( examResult.getQuestionResults().contains( result ) ) {
            resultForView = result;
        }
        if ( request.isXHR() ) {
            ajaxResponseRenderer.addRender( resultViewZone );
            ajaxResponseRenderer.addRender( questionListZone );
        }
    }

}
