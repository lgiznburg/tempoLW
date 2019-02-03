package ru.rsmu.tempoLW.pages.admin;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import ru.rsmu.tempoLW.consumabales.CrudMode;

/**
 * @author leonid.
 */
@RequiresRoles("admin")
public class Users {
    @Property
    @ActivationRequestParameter
    private CrudMode mode;

    @Property
    @ActivationRequestParameter
    private Long userId;

    @Property
    private Long listUserId;

    public void setupRender() {
        listUserId = userId;
    }

    public void onToCreate() {
        mode = CrudMode.CREATE;
        userId = null;
    }

    public void onUserSelectedFromList( Long selectedUserId ) {
        userId = selectedUserId;
        mode = CrudMode.REVIEW;
    }

    //------ Create

    void onCancelCreateFromEditor() {
        mode = null;
        userId = null;
    }

    void onCreatedFromEditor( Long createdUserId ) {
        mode = CrudMode.REVIEW;
        userId = createdUserId;
    }

    //--------- Update

    void onCancelUpdateFromEditor( Long updatedUserId ) {
        mode = CrudMode.REVIEW;
        userId = updatedUserId;
    }

    void onUpdatedFromEditor( Long updatedUserId ) {
        mode = CrudMode.REVIEW;
        userId = updatedUserId;
    }

    //----------- Review

    void onToUpdateFromEditor( Long updatedUserId ) {
        mode = CrudMode.UPDATE;
        userId = updatedUserId;
    }

}
