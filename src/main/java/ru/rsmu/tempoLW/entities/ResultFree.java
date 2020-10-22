package ru.rsmu.tempoLW.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

/**
 * @author leonid.
 */
@Entity
@DiscriminatorValue( "FREE" )
public class ResultFree extends ResultElement {
    private static final long serialVersionUID = -2592186038932404976L;

    @OneToMany( mappedBy = "resultElement")
    private List<ResultAttachedFile> files;

    @OneToMany( mappedBy = "resultElement")
    private List<ResultEvaluation> evaluation;

    @Override
    public void checkCorrectness() {

    }

    public List<ResultAttachedFile> getFiles() {
        return files;
    }

    public void setFiles( List<ResultAttachedFile> files ) {
        this.files = files;
    }

    public List<ResultEvaluation> getEvaluation() {
        return evaluation;
    }

    public void setEvaluation( List<ResultEvaluation> evaluation ) {
        this.evaluation = evaluation;
    }
}
