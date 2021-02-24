package ru.rsmu.tempoLW.components;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
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

    @Inject
    private QuestionDao questionDao;

    @Inject
    private LinkSource linkSource;

    @Inject
    Block questionSimple, questionOpen, questionCorrespondence,
            questionSimpleOrder, questionTree, questionFree, questionTreeOpen,
            questionCrossword, questionBigOpen;

    public Block getQuestionBlock() {
        Block block = null;
        switch ( current.getQuestion().getType() ) {
            case OPEN:
                block = questionOpen;
                break;
            case SIMPLE:
                block = questionSimple;
                break;
            case SIMPLE_ORDER:
                block = questionSimpleOrder;
                break;
            case CORRESPONDENCE:
                block = questionCorrespondence;
                break;
            case TREE:
                block = questionTree;
                break;
            case FREE:
                block = questionFree;
                break;
            case TREE_OPEN:
                block = questionTreeOpen;
                break;
            case CROSSWORD:
                block = questionCrossword;
                break;
            case BIG_OPEN:
                block = questionBigOpen;
                break;
        }
        return block;
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


    public boolean isReferencesOrCalculatorPresent() {
        ExamSubject subject = current.getQuestion().getQuestionInfo().getSubject();
        /*if ( !Hibernate.isInitialized( subject.getReferenceMaterials() ) ) {
            questionDao.refresh( subject );
        }*/
        long materialsCount = questionDao.findReferencesCount( subject );
        return subject.isUseCalculator()
                || materialsCount > 0;
                //( subject.getReferenceMaterials() != null && subject.getReferenceMaterials().size() > 0 );
    }

    public ExamSubject getSubject() {
        return current.getQuestion().getQuestionInfo().getSubject();
    }
}
