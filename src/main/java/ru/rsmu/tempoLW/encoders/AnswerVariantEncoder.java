package ru.rsmu.tempoLW.encoders;

import org.apache.tapestry5.ValueEncoder;
import ru.rsmu.tempoLW.dao.QuestionDao;
import ru.rsmu.tempoLW.entities.AnswerVariant;

/**
 * @author leonid.
 */
public class AnswerVariantEncoder implements ValueEncoder<AnswerVariant> {

    private QuestionDao questionDao;

    public AnswerVariantEncoder( QuestionDao questionDao ) {
        this.questionDao = questionDao;
    }

    @Override
    public String toClient( AnswerVariant value ) {
        return String.valueOf( value.getId() );
    }

    @Override
    public AnswerVariant toValue( String clientValue ) {
        return questionDao.find( AnswerVariant.class, Long.parseLong( clientValue ) );
    }
}
