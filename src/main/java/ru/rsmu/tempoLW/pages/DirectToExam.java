package ru.rsmu.tempoLW.pages;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tynamo.security.services.SecurityService;

/**
 * @author leonid.
 */
public class DirectToExam {

    @ActivationRequestParameter( "token" )
    String token;

    @Property
    @SessionState
    private String examKey;

    @Inject
    private SecurityService securityService;


    public Object onActivate() {
        //String decoded = new String( Base64.getDecoder().decode( token.getBytes( StandardCharsets.UTF_8 ) ) );
        String[] logopass = token.split( ":" );
        if ( logopass.length == 2 ) {
            UsernamePasswordToken upt = new UsernamePasswordToken(logopass[0], logopass[1]);
            upt.setHost( "direct" );

            Subject currentUser = securityService.getSubject();

            if (currentUser == null)
            {
                throw new IllegalStateException("Subject can't be null");
            }

            try {
                currentUser.login( upt );

                // save encrypted password in session to select correct exam
                examKey = logopass[1];
            } catch (AuthenticationException e) {
                // log unauthorized try to login
            }

        }
        return Index.class;
    }
}
