package ru.rsmu.tempoLW.entities;

import javax.persistence.*;
import java.util.List;

/**
 * @author leonid.
 */
@Entity
@DiscriminatorValue( "BIG_OPEN" )
public class ResultBigOpen extends ResultElement {
    private static final long serialVersionUID = -7705941974493685611L;

    @OneToOne( mappedBy = "resultElement",cascade = CascadeType.ALL)
    private AttachedLongText text;

    @OneToMany( mappedBy = "resultElement")
    private List<ResultEvaluation> evaluation;

    @Override
    public void checkCorrectness() {

    }

    public AttachedLongText getText() {
        return text;
    }

    public void setText( AttachedLongText text ) {
        this.text = text;
    }

    public List<ResultEvaluation> getEvaluation() {
        return evaluation;
    }

    public void setEvaluation( List<ResultEvaluation> evaluation ) {
        this.evaluation = evaluation;
    }
}
