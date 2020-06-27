package ru.rsmu.tempoLW.pages.proctoring;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.HttpError;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import ru.rsmu.tempoLW.dao.SystemPropertyDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.Testee;
import ru.rsmu.tempoLW.entities.system.StoredPropertyName;
import ru.rsmu.tempoLW.services.SecurityUserHelper;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leonid.
 */
@RequiresUser
public class Token {

    private static final String JWT_PROVIDER = "jwt";
    private static final String PLAIN_PROVIDER = "plain";

    @SessionState
    private ExamResult examResult;

    @Inject
    private SecurityUserHelper securityUserHelper;

    @Inject
    private SystemPropertyDao systemPropertyService;

    @Inject
    private Request request;

    @Inject
    @Symbol(SymbolConstants.CONTEXT_PATH)
    private String contextPath;

    public Object onActivate() {

        Testee testee = securityUserHelper.getCurrentTestee();

        if (  testee == null || examResult == null || examResult.getTestee().getId() != testee.getId() ) {
            // I'm paranoid a little bit
            return new HttpError( HttpServletResponse.SC_NOT_FOUND, "No testee nor exam found" );
        }

        String secretString = systemPropertyService.getProperty( StoredPropertyName.PROCTORING_SECRET_KEY );
        Algorithm algorithmHS = Algorithm.HMAC256( secretString );

        String token;
        try {
            token = buildPayload( testee )
                    .sign( algorithmHS );
        } catch (JWTCreationException exception){
            //Invalid Signing configuration / Couldn't convert Claims.
            return new HttpError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Can't create token or convert it into JSON" );
        }

        return new StreamResponse() {
            @Override
            public String getContentType() {
                return "text/plain";
            }

            @Override
            public InputStream getStream() throws IOException {
                return new ByteArrayInputStream( token.getBytes( StandardCharsets.UTF_8 ) );
            }

            @Override
            public void prepareResponse( Response response ) {
                response.setContentLength( token.getBytes( StandardCharsets.UTF_8 ).length );
            }
        };

    }

    private JWTCreator.Builder buildPayload( Testee testee ) {
        ExamSchedule exam = examResult.getExam();

        String sessionId = String.format( "%s-%s-%d", testee.getLogin(),
                testee.getCaseNumber(),
                exam.getId() );
        sessionId = sessionId.replaceAll( " ", "" );

        Map<String,Object> payload = new HashMap<>();

        Calendar expiration = Calendar.getInstance();
        if ( examResult.getStartTime() != null ) {
            expiration.setTime( examResult.getStartTime() ); // in case of restart
        }
        expiration.add( Calendar.HOUR, exam.getDurationHours() );
        expiration.add( Calendar.MINUTE, exam.getDurationMinutes() + 20 ); //extra 20 min
        JWTCreator.Builder jwtBuilder = JWT.create()
                .withExpiresAt( expiration.getTime() )
                .withClaim( "username", testee.getCaseNumber() )
                //.withClaim( "role", "student" )
                .withClaim( "nickname", testee.getLastName() )
                .withClaim( "id", sessionId )      // proctoring session
                .withClaim( "subject", exam.getName() + " - " + exam.getTestingPlan().getSubject().getTitle() ); // session name

        if ( systemPropertyService.getPropertyAsInt( StoredPropertyName.PROCTORING_CALLBACK_ALLOWED ) > 0 ) {
            String schema = "https://"; //request.isSecure() ? "https://" : "http://";
            String server = request.getServerName(); //"85.142.163.82";
            int port = request.getServerPort();
            StringBuilder callbackUri = new StringBuilder();
            callbackUri.append( schema ).append( server );
            if ( port != 0 && port != 80 ) callbackUri.append( ":" ).append( port );
            callbackUri.append( contextPath ).append( "/rest/proctoring/result" );
            jwtBuilder.withClaim( "api", callbackUri.toString() );   // callback address
        }

        return jwtBuilder;
    }
}
