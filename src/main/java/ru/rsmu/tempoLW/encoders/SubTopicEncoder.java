package ru.rsmu.tempoLW.encoders;

import org.apache.tapestry5.ValueEncoder;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.SubTopic;

/**
 * @author leonid.
 */
public class SubTopicEncoder implements ValueEncoder<SubTopic> {

    private QuestionDao questionDao;

    public SubTopicEncoder( QuestionDao questionDao ) {
        this.questionDao = questionDao;
    }

    @Override
    public String toClient( SubTopic value ) {
        return String.valueOf( value.getId() );
    }

    @Override
    public SubTopic toValue( String clientValue ) {
        return questionDao.find( SubTopic.class, Long.parseLong( clientValue ) );
    }
}
