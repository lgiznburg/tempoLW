package ru.rsmu.tempoLW.components.admin.appeal;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.entities.*;
import ru.rsmu.tempoLW.pages.QuestionImage;

public class ResultView {
    @Parameter(required = true)
    @Property
    private QuestionResult current;

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
}
