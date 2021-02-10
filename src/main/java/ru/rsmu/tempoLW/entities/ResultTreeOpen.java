package ru.rsmu.tempoLW.entities;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author leonid.
 */
@Entity
@DiscriminatorValue( "TREE_OPEN" )
public class ResultTreeOpen extends ResultElement {
    private static final long serialVersionUID = -2919793413593945341L;

    @ManyToOne
    @JoinColumn(name = "correspondence_variant_id")
    private CorrespondenceVariant correspondenceVariant;

    @Column
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public CorrespondenceVariant getCorrespondenceVariant() {
        return correspondenceVariant;
    }

    public void setCorrespondenceVariant( CorrespondenceVariant correspondenceVariant ) {
        this.correspondenceVariant = correspondenceVariant;
    }

    @Override
    public void checkCorrectness() {
        //it's not important. correctness is defined in QuestionResult using Question::countErrors
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !(o instanceof ResultTreeOpen) ) return false;
        if ( !super.equals( o ) ) return false;
        ResultTreeOpen that = (ResultTreeOpen) o;
        return correspondenceVariant.equals( that.correspondenceVariant ) &&
                Objects.equals( value, that.value );
    }

    @Override
    public int hashCode() {
        return Objects.hash( super.hashCode(), correspondenceVariant, value );
    }
}
