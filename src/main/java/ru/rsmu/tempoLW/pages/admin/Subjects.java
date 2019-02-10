package ru.rsmu.tempoLW.pages.admin;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.hibernate.HibernateGridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;
import ru.rsmu.tempoLW.consumabales.CrudMode;
import ru.rsmu.tempoLW.entities.ExamSubject;

/**
 * @author leonid.
 */
@RequiresRoles(value = {"admin","subject_admin"}, logical = Logical.OR )
public class Subjects {

    @Property
    @ActivationRequestParameter
    private CrudMode mode;

    @Property
    @ActivationRequestParameter
    private Long subjectId;

    public void set( Long subjectId ) {
        this.subjectId = subjectId;
        if ( this.subjectId != null ) {
            this.mode = CrudMode.REVIEW;
        }
    }

    public boolean isMode( String modeName ) {
        return mode != null && mode.name().equals( modeName );
    }

    public void onSubjectSelected( Long selectedSubjectId ) {
        mode = CrudMode.REVIEW;
        subjectId = selectedSubjectId;
    }

    public void onToCreate(){
        mode = CrudMode.CREATE;
        subjectId = null;
    }

    //  Create
    public void onSubjectCreated( Long createdSubjectId ) {
        mode = CrudMode.REVIEW;
        subjectId = createdSubjectId;
    }

    public void onCancelEditFromSubjectCreate( ) {
        mode = null;
        subjectId = null;
    }


    // Review
    public void onToUploadFromSubjectReview( Long updateSubjectId ) {
        mode = CrudMode.UPLOAD;
        subjectId = updateSubjectId;
    }

    public void onToUpdateFromSubjectReview( Long updateSubjectId ) {
        mode = CrudMode.UPDATE;
        subjectId = updateSubjectId;
    }

    // Update
    public void onSubjectEdited( Long editedSubjectId ) {
        mode = CrudMode.REVIEW;
        subjectId = editedSubjectId;
    }

    public void onCancelEditFromSubjectUpdate( Long editedSubjectId ) {
        mode = CrudMode.REVIEW;
        subjectId = editedSubjectId;
    }



    // Upload
    public void onUploadDone( Long uploadSubjectId ) {  // FromUploadQuestions
        mode = CrudMode.REVIEW;
        subjectId = uploadSubjectId;
    }

    public void onCancelUpload( Long uploadSubjectId ) { //FormUploadQuestions
        mode = CrudMode.REVIEW;
        subjectId = uploadSubjectId;
    }

}
