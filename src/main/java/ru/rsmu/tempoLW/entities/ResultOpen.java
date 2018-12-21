package ru.rsmu.tempoLW.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author leonid.
 */
@Entity
@DiscriminatorValue( "OPEN" )
public class ResultOpen extends ResultElement {
    private static final long serialVersionUID = 3274243361711131923L;

    @Column
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    @Override
    public void checkCorrectness() {
        //it's not important. correctness is defined in QuestionResult
    }
}
