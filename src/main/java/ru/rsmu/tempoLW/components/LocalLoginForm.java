package ru.rsmu.tempoLW.components;

import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.ValidationException;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.security.services.SecurityService;

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

    @Inject
    private Messages messages;

    @Inject
    private SecurityService securityService;

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
            loginMessage = messages.get("realm.wrong-credentials");
        } catch (LockedAccountException e)
        {
            loginMessage = messages.get("realm.account-locked");
        } catch (ExpiredCredentialsException e)
        {
            loginMessage = messages.get("realm.account-expired");
        } catch (AuthenticationException e)
        {
            loginMessage = messages.get("realm.error");
        }

        if (loginMessage != null)
        {
            throw new ValidationException(loginMessage);
        }
    }

    public void onSuccessFromTempoLoginForm() {

    }

}
