package ru.rsmu.tempoLW.components.admin;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.LocalizationSetter;
import org.apache.tapestry5.util.AbstractSelectModel;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.pages.admin.Subjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


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

    @Inject
    private LocalizationSetter localizationSetter;

    @Inject
    private Messages messages;

    public void onPrepareForRender() {
        if ( subjectForm.isValid() ) {
            prepare();
        }
    }

    public void onPrepareForSubmit() {
        prepare();
    }

    private void prepare() {
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

    public SelectModel getLocalesModel() {
        return new AbstractSelectModel() {
            @Override
            public List<OptionGroupModel> getOptionGroups() {
                return null;
            }

            @Override
            public List<OptionModel> getOptions() {
                List<OptionModel> options = new ArrayList<>();
                for ( Locale locale : localizationSetter.getSupportedLocales() ) {
                    options.add( new OptionModelImpl( locale.getLanguage().equals("en") ? messages.get("language-english") : messages.get("language-russian"), locale.getLanguage())); //StringUtils.capitalize( locale.getDisplayLanguage() )
                }

                return options;
            }
        };
    }
}
