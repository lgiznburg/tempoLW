package ru.rsmu.tempoLW.pages.admin;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.tempoLW.consumabales.CrudMode;

/**
 * CRUD controller for ExamSchedule management
 * @author leonid.
 */
@RequiresRoles(value = {"admin","subject_admin", "teacher"}, logical = Logical.OR )
public class Exams {

    @Property
    @ActivationRequestParameter
    private CrudMode mode;

    @Property
    @ActivationRequestParameter
    private Long examId;

    public void set( Long examId ) {
        this.examId = examId;
        if ( this.examId != null ) {
            this.mode = CrudMode.REVIEW;
        }
    }

    public boolean isMode( String modeName ) {
        return mode != null && mode.name().equals( modeName );
    }

    public void onExamSelected( Long selectedExamId ) {
        examId = selectedExamId;
        mode = CrudMode.REVIEW;
    }

    public void onToCreate() {
        examId = null;
        mode = CrudMode.CREATE;
    }

    //  Create
    public void onExamCreated( Long createdExamId ) {
        mode = CrudMode.REVIEW;
        examId = createdExamId;
    }

    public void onCancelEditFromExamCreate( ) {
        mode = null;
        examId = null;
    }


    // Review
    public void onToUpload( Long updateExamId ) {
        mode = CrudMode.UPLOAD;
        examId = updateExamId;
    }

    public void onToUpdate( Long updateExamId ) {
        mode = CrudMode.UPDATE;
        examId = updateExamId;
    }

    public void onToAddTestee( Long updateExamId ) {
        mode = CrudMode.ADD_ELEMENT;
        examId = updateExamId;
    }

    // Update
    public void onExamUpdated( Long editedExamId ) {
        mode = CrudMode.REVIEW;
        examId = editedExamId;
    }

    public void onCancelEditFromExamUpdate( Long editedExamId ) {
        mode = CrudMode.REVIEW;
        examId = editedExamId;
    }

    // Upload
    public void onUploadDone( Long uploadExamId ) {  // FromUploadQuestions
        mode = CrudMode.REVIEW;
        examId = uploadExamId;
    }

    public void onCancelUpload( Long uploadExamId ) { //FormUploadQuestions
        mode = CrudMode.REVIEW;
        examId = uploadExamId;
    }

    // Testees list
    public void onToTesteesList( Long listExamId ) {
        mode = CrudMode.VIEW_ELEMENTS;
        examId = listExamId;
    }

    public void onCancelTesteesFromExamTestees( Long selectedExamId ) {
        examId = selectedExamId;
        mode = CrudMode.REVIEW;
    }

    // single Testee addition

    public void onCancelAddFromExamCreateTestee( Long selectedExamId ) {
        examId = selectedExamId;
        mode = CrudMode.REVIEW;
    }

    public void onTesteeAdded (Long selectedExamId) {
        examId = selectedExamId;
        mode = CrudMode.REVIEW;
    }

}
