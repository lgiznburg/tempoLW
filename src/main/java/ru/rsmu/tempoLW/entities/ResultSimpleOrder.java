package ru.rsmu.tempoLW.entities;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author leonid.
 */
public class ResultSimpleOrder extends ResultElement {
    private static final long serialVersionUID = 3769942100319644254L;

    @Column(name = "entered_order")
    private int enteredOrder = 0;

    @ManyToOne
    @JoinColumn(name = "answer_variant_id")
    private AnswerVariant answerVariant;

    public AnswerVariant getAnswerVariant() {
        return answerVariant;
    }

    public void setAnswerVariant( AnswerVariant answerVariant ) {
        this.answerVariant = answerVariant;
    }

    public int getEnteredOrder() {
        return enteredOrder;
    }

    public void setEnteredOrder( int enteredOrder ) {
        this.enteredOrder = enteredOrder;
    }

    @Override
    public void checkCorrectness() {
        setCorrect( answerVariant.isCorrect() && answerVariant.getSequenceOrder() == enteredOrder );
    }
}
