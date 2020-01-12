package ru.rsmu.tempoLW.pages.admin;

import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import ru.rsmu.tempoLW.consumabales.CrudMode;
import ru.rsmu.tempoLW.dao.ExamDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.QuestionResult;
import ru.rsmu.tempoLW.entities.Testee;

import java.util.HashMap;
import java.util.Map;

// ann
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

    @Property
    private QuestionResult questionResult;

    @ActivationRequestParameter
    private Long questionId = null;

    @Property
    private QuestionResult resultForView;

    @Environmental
    private JavaScriptSupport javaScriptSupport;

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

    public Object onActivate() {
        examResult = examDao.findExamResultForTestee(exam,testee);
        if (questionId == null){
            resultForView = examResult.getQuestionResults().get(0);
        }
        else {
            for (QuestionResult qR : examResult.getQuestionResults()) {
                if (questionId == qR.getId()) {
                    resultForView = qR;
                    break;
                }
            }
        }
        return null;
    }

    public void setupRender() {
        javaScriptSupport.require( "katex/katex" );
        javaScriptSupport.require( "katex/contrib/auto-render" ).invoke( "renderMathInElementOfClass" ).with( "container" );
    }
}
