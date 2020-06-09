package ru.rsmu.tempoLW.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Hibernate;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.pages.QuestionImage;

/**
 * @author leonid.
 */
public class QuestionForm {

    @Parameter(required = true)
    @Property
    private QuestionResult current;

    /**
     * for interaction through subject.referenceMaterials
     */
    @Property
    private SubjectReferenceMaterial referenceMaterial;

    @Inject
    private QuestionDao questionDao;

    @Inject
    private LinkSource linkSource;

    @Inject
    Block questionSimple, questionOpen, questionCorrespondence, questionSimpleOrder, questionTree;

    public Block getQuestionBlock() {
        Question question = current.getQuestion();
        if ( question instanceof QuestionSimple ) {
            return questionSimple;
        }
        else if ( question instanceof QuestionOpen ) {
            return questionOpen;
        }
        else if ( question instanceof QuestionCorrespondence ) {
            return questionCorrespondence;
        }
        else if ( question instanceof QuestionSimpleOrder ) {
            return questionSimpleOrder;
        }
        else if ( question instanceof QuestionTree ) {
            return questionTree;
        }
        return null;
    }

    public int getReadableNumber() {
        return current.getOrderNumber() + 1;
    }

    public String getImageLink() {
        if ( current.getQuestion().getImage() != null ) {
            return linkSource.createPageRenderLink( QuestionImage.class.getSimpleName(), false, current.getQuestion().getImage().getId() ).toURI();
        }
        return "";
    }

    public String getReferenceImageLink() {
        if ( referenceMaterial != null ) {
            return linkSource.createPageRenderLink( QuestionImage.class.getSimpleName(), false, referenceMaterial.getImage().getId() ).toURI();
        }
        return "";
    }

    public boolean isReferencesOrCalculatorPresent() {
        ExamSubject subject = current.getQuestion().getQuestionInfo().getSubject();
        if ( !Hibernate.isInitialized( subject.getReferenceMaterials() ) ) {
            questionDao.refresh( subject );
        }
        return subject.isUseCalculator()
                || ( subject.getReferenceMaterials() != null && subject.getReferenceMaterials().size() > 0 );
    }

    public ExamSubject getSubject() {
        return current.getQuestion().getQuestionInfo().getSubject();
    }
}
