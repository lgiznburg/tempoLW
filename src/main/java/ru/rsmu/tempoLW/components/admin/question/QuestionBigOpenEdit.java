package ru.rsmu.tempoLW.components.admin.question;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.entities.QuestionFree;
import ru.rsmu.tempoLW.pages.admin.question.QuestionList;
import ru.rsmu.tempoLW.pages.admin.question.QuestionView;

/**
 * @author leonid.
 */
public class QuestionBigOpenEdit {
    @Parameter( required = true )
    @Property
    private Question question;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private LinkSource linkSource;

    @InjectPage
    private QuestionList questionList;

    private boolean applySubmit = false;

    public QuestionFree getQuestionFree() {
        return (QuestionFree) question;
    }

    public void onSelectedFromApply() {
        applySubmit = true;
    }

    public Object onSuccess() {
        questionDao.save( question );
        if ( applySubmit ) {
            return null;
        }
        return linkSource.createPageRenderLink( "admin/question/" + QuestionView.class.getSimpleName(), false, question );
    }

    public Object onToQuestionList() {
        questionList.set( question.getQuestionInfo().getSubject() );
        return questionList;
    }

}
