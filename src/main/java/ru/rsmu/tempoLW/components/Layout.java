package ru.rsmu.tempoLW.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.security.services.SecurityService;
import ru.rsmu.tempoLW.entities.auth.User;
import ru.rsmu.tempoLW.pages.Index;
import ru.rsmu.tempoLW.pages.Login;
import ru.rsmu.tempoLW.services.SecurityUserHelper;

/**
 * @author leonid.
 */
@Import(stylesheet = {"context:/static/css/jquery-ui.min.css", "context:/static/css/tempolw.css",
    "context:/static/css/ubuntu.css"},
        module = {"bootstrap/dropdown"})
public class Layout {

    @Property
    @Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
    private String pageTitle;

    @Inject
    private SecurityService securityService;

    @Inject
    private SecurityUserHelper securityUserHelper;

    public String getUsername() {
        User user = securityUserHelper.getCurrentUser();
        return user != null ? user.getFirstName() + " " +user.getMiddleName() : "";
    }

    /**
     * Respond to the user clicking on the "Log Out" link
     */
    //@Log
    public Object onLogout()
    {
        if ( securityService.isUser() ) {
            securityService.getSubject().logout();
            return Index.class;
        }
        else {
            return Login.class;
        }
    }

}
