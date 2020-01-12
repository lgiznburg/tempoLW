package ru.rsmu.tempoLW.components.admin.appeal;

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

    public boolean isQuestionSimple() {
        return current.getQuestion() instanceof QuestionSimple;
    }

    public boolean isQuestionOpen() {
        return current.getQuestion() instanceof QuestionOpen;
    }

    public boolean isQuestionCorrespondence() {
        return current.getQuestion() instanceof QuestionCorrespondence;
    }

    public boolean isQuestionSimpleOrder() {
        return current.getQuestion() instanceof QuestionSimpleOrder;
    }

    public boolean isQuestionTree() {
        return current.getQuestion() instanceof QuestionTree;
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
