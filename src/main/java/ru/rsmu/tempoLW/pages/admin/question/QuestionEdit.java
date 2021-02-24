package ru.rsmu.tempoLW.pages.admin.question;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.LinkSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ValueEncoderSource;
import org.apache.tapestry5.upload.services.UploadedFile;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.*;

import java.util.List;

/**
 * @author leonid.
 */
@RequiresRoles(value = {"admin","subject_admin"}, logical = Logical.OR )
public class QuestionEdit {
    @Property
    @PageActivationContext
    private Question question;

    @Property
    private SelectModel topicModel;

    @Property
    private UploadedFile imageFile;

    @Inject
    private SelectModelFactory modelFactory;

    @Inject
    private QuestionDao questionDao;

    private List<SubTopic> topicList;

    @Inject
    private LinkSource linkSource;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    @InjectPage
    private QuestionList questionList;

    @Inject
    Block questionSimple, questionOpen, questionCorrespondence,
            questionSimpleOrder, questionTree, questionFree, questionTreeOpen,
            questionCrossword, questionBigOpen;

    public Block getQuestionBlock() {
        Block block = null;
        switch ( question.getType() ) {
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


}
