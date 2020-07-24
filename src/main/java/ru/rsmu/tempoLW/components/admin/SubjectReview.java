package ru.rsmu.tempoLW.components.admin;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.OptionModelImpl;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.LocalizationSetter;
import org.apache.tapestry5.util.AbstractSelectModel;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.entities.SubTopic;
import ru.rsmu.tempoLW.entities.SubjectReferenceMaterial;
import ru.rsmu.tempoLW.entities.TestingPlan;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author leonid.
 */
public class SubjectReview {
    @Parameter(required = true)
    @Property
    private Long subjectId;


    // screen feilds
    @Property
    private ExamSubject subject;

    @Property
    private List<TestingPlan> plans;

    @Property
    private TestingPlan plan;

    @Property
    private List<SubTopic> topics;

    @Property
    private SubTopic topic;

    /**
     * object for iteration through subject.referenceMaterials
     */
    @Property
    private SubjectReferenceMaterial referenceMaterial;

    @Inject
    private LocalizationSetter localizationSetter;

    // other
    @Inject
    private QuestionDao questionDao;

    @Inject
    private Messages messages;

    public void setupRender() {
        subject = subjectId != null ? questionDao.find( ExamSubject.class, subjectId ) : null;

        if ( subject != null ) {
            plans = questionDao.findTestingPlan( subject );
            topics = questionDao.findTopicsOfSubject( subject );
        }
    }

    public long getQuestionsCount() {
        if ( subject != null ) {
            return  questionDao.findQuestionsCount( subject );
        }
        return 0;
    }

    public long getTopicsCount() {
        if ( subject != null ) {
            return  questionDao.findTopicsCount( subject );
        }
        return 0;
    }

    public String getLanguageName() {
        for ( Locale locale : localizationSetter.getSupportedLocales() ) {
            if ( subject.getLocale().equals( locale.getLanguage() ) ) {
                return StringUtils.capitalize( locale.getDisplayLanguage() );
            }
        }
        return "";
    }

    public String getSubjectLevel() {
        return messages.get( subject.getType().name() );
    }

    public boolean isReferenceMaterialsPresent() {
        return subject.getReferenceMaterials() != null && subject.getReferenceMaterials().size() > 0;
    }
}
