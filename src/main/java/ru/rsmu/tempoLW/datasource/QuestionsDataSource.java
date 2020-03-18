package ru.rsmu.tempoLW.datasource;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.hibernate.HibernateGridDataSource;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.tempoLW.entities.ExamSubject;
import ru.rsmu.tempoLW.entities.SubTopic;

/**
 * @author leonid.
 */
public class QuestionsDataSource extends HibernateGridDataSource {

    private ExamSubject subject;

    private SubTopic topic;

    private Integer complexity;

    private Integer maxScore;

    private String text;

    public QuestionsDataSource( Session session, Class entityType, ExamSubject subject, SubTopic topic, Integer complexity, Integer maxScore, String text ) {
        super( session, entityType );
        this.subject = subject;
        this.topic = topic;
        this.complexity = complexity;
        this.maxScore = maxScore;
        this.text = text;
    }

    @Override
    protected void applyAdditionalConstraints( Criteria criteria ) {
        criteria.createAlias( "questionInfo", "questionInfo" )
                .add( Restrictions.eq( "questionInfo.subject", subject ) );
        if ( topic != null ) {
            criteria.add( Restrictions.eq( "questionInfo.topic", topic ) );
        }
        if ( complexity != null ) {
            criteria.add( Restrictions.eq( "questionInfo.complexity", complexity ) );
        }
        if ( maxScore != null ) {
            criteria.add( Restrictions.eq( "questionInfo.maxScore", maxScore ) );
        }
        if ( StringUtils.isNoneBlank( text ) ) {
            criteria.add( Restrictions.like( "text", "%"+text + "%" ) );
        }

    }
}
