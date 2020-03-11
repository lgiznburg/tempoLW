package ru.rsmu.tempoLW.pages.admin.question;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ValueEncoderSource;
import org.hibernate.Session;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.datasource.QuestionsDataSource;
import ru.rsmu.tempoLW.encoders.SubTopicEncoder;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.entities.Question;
import ru.rsmu.tempoLW.entities.SubTopic;

/**
 * @author leonid.
 */
@RequiresRoles(value = {"admin","subject_admin","teacher"}, logical = Logical.OR )
public class QuestionList {
    @Property
    @Parameter(required = true, allowNull = false)
    private ExamSubject subject;

    //
    @Property
    private SubTopic topic;

    @Property
    private Integer complexity;

    @Property
    private Integer maxScore;

    @Property
    private String text;

    /**
     * Property for interaction through the grid
     */
    @Property
    private Question question;

    //=============
    @Inject
    private QuestionDao questionDao;

    @Inject
    private SelectModelFactory modelFactory;

    @Inject
    private ValueEncoderSource valueEncoderSource;

    @Inject
    private Session session;

    public ValueEncoder<SubTopic> getTopicEncoder() {
        return valueEncoderSource.getValueEncoder( SubTopic.class );
    }

    public SelectModel getTopicModel() {
        return modelFactory.create( questionDao.findTopicsOfSubject( subject ), "title" );
    }

    public QuestionsDataSource getQuestionDataSource() {
        return new QuestionsDataSource(session, Question.class, topic, complexity, maxScore, text );
    }
}
