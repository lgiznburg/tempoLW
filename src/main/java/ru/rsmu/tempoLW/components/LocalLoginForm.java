package ru.rsmu.tempoLW.components;

import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Request;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.internal.services.LoginContextService;
import org.tynamo.security.services.SecurityService;
import ru.rsmu.tempoLW.dao.UserDao;
import ru.rsmu.tempoLW.entities.auth.UserRoleName;

import java.io.IOException;

/**
 * @author leonid.
 */
public class LocalLoginForm {

    @Parameter
    @Property
    private boolean includeRememberMe = true;

    @Property
    private String login;

    @Property
    private String password;

    @Property
    private boolean rememberMe;

    @Property
    private String loginMessage;

    @Property
    @SessionState
    private String examKey;

    @Inject
    private Messages messages;

    @Inject
    private SecurityService securityService;

    @Inject
    private LoginContextService loginContextService;

    @Inject
    @Symbol(SecuritySymbols.REDIRECT_TO_SAVED_URL)
    private boolean redirectToSavedUrl;

    @Inject
    private Request request;

    @Inject
    private UserDao userDao;


    public void onValidateFromTempoLoginForm() throws ValidationException {
        Subject currentUser = securityService.getSubject();

        if (currentUser == null)
        {
            throw new IllegalStateException("Subject can't be null");
        }

        UsernamePasswordToken token = new UsernamePasswordToken(login, password);
        token.setRememberMe(rememberMe);

        try {
            currentUser.login( token );
        } catch (UnknownAccountException | IncorrectCredentialsException e)
        {
            loginMessage = messages.get("realm.wrong-credentials"); //no user
        } catch (LockedAccountException e)
        {
            loginMessage = messages.get("realm.account-locked");  //exam day
        } catch (ExpiredCredentialsException e)
        {
            loginMessage = messages.get("realm.account-expired"); //expired
        } catch ( DisabledAccountException e )
        {
            loginMessage = e.getMessage();  // exam period
        } catch (AuthenticationException e)
        {
            loginMessage = messages.get("realm.error"); // general error
        }

        if (loginMessage != null)
        {
            throw new ValidationException(loginMessage);
        }
        // save encrypted password in session to select correct exam
        if ( !securityService.isUser() || securityService.hasRole( UserRoleName.testee.name() ) ) {
            examKey = userDao.encrypt( password );
        }
    }

    public Object onSuccessFromTempoLoginForm() throws IOException {
        String localePath = loginContextService.getLocaleFromPath( request.getPath() );
        String successUrl = loginContextService.getSuccessURL();
        if ( localePath != null && !successUrl.startsWith( "http" ) ) {
            if ( !successUrl.startsWith( "/" )  ) {
                successUrl = "/" + successUrl;
            }
            successUrl = localePath + successUrl;
        }
        if (redirectToSavedUrl) {
            String requestUri = successUrl;
            if (!requestUri.startsWith("/") && !requestUri.startsWith("http")) {
                requestUri = "/" + requestUri;
            }
            loginContextService.redirectToSavedRequest(requestUri);
            return null;
        }
        return successUrl;

    }

}
