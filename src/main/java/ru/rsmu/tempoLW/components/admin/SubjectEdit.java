package ru.rsmu.tempoLW.components.admin;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.pages.admin.Subjects;


/**
 * @author leonid.
 */
public class SubjectEdit {
    @Property
    @Parameter(required = true)
    private Long subjectId;

    @Property
    private ExamSubject subject;

    @Inject
    private QuestionDao questionDao;

    @InjectComponent("subjectForm")
    private Form subjectForm;

    @Inject
    private ComponentResources componentResources;


    public void onPrepareForRender() {
        if ( subjectForm.isValid() ) {
            if ( subjectId != null ) {
                subject = questionDao.find( ExamSubject.class, subjectId );
            }

            if ( subject == null ) {
                subject = new ExamSubject();
            }
        }
    }

    public void onPrepareForSubmit() {
        if ( subjectId != null ) {
            subject = questionDao.find( ExamSubject.class, subjectId );
        }

        if ( subject == null ) {
            subject = new ExamSubject();
        }
    }

    public boolean onSuccess() {
        String event = subject.getId() != 0 ? "subjectUpdated" : "subjectCreated";
        questionDao.save( subject );
        componentResources.triggerEvent( event, new Object[]{subject.getId()}, null );

        return true;
    }

    public String getTitle() {
        return subject.getId() != 0 ? "Edit" : "Create";
    }
}
