package ru.rsmu.tempoLW.pages.admin;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.TestSubject;

import javax.inject.Inject;

/**
 * @author leonid.
 */
public class EditSubject {
    @Property
    @PageActivationContext
    private Long subjectId;

    @Property
    private TestSubject subject;

    @Inject
    private QuestionDao questionDao;

    @InjectComponent("subjectForm")
    private BeanEditForm subjectForm;


    public void onPrepareForRender() {
        if ( subjectForm.isValid() ) {
            if ( subjectId != null ) {
                subject = questionDao.find( TestSubject.class, subjectId );
            }

            if ( subject == null ) {
                subject = new TestSubject();
            }
        }
    }

    public void onPrepareForSubmit() {
        if ( subjectId != null ) {
            subject = questionDao.find( TestSubject.class, subjectId );
        }

        if ( subject == null ) {
            subject = new TestSubject();
        }
    }

    public Object onSuccess() {
        questionDao.save( subject );

        return Subjects.class;
    }
}
