package ru.rsmu.tempoLW.pages.proctoring;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.HttpError;
import org.apache.tapestry5.services.Response;
import ru.rsmu.tempoLW.dao.SystemPropertyDao;
import ru.rsmu.tempoLW.entities.ExamResult;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.Testee;
import ru.rsmu.tempoLW.entities.system.StoredPropertyName;
import ru.rsmu.tempoLW.services.SecurityUserHelper;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leonid.
 */
@RequiresUser
public class Token {

    @SessionState
    private ExamResult examResult;

    @Inject
    private SecurityUserHelper securityUserHelper;

    @Inject
    private SystemPropertyDao systemPropertyService;

    public Object onActivate() {

        Testee testee = securityUserHelper.getCurrentTestee();

        if (  testee == null || examResult == null || examResult.getTestee().getId() != testee.getId() ) {
            // I'm paranoid a little bit
            return new HttpError( HttpServletResponse.SC_NOT_FOUND, "No testee nor exam found" );
        }
        ExamSchedule exam = examResult.getExam();

        String sessionId = testee.getLogin() + exam.getName();
        sessionId = sessionId.replaceAll( " ", "" );

        Map<String,Object> payload = new HashMap<>();
        payload.put( "exp", 4*60*60 );  // 4 hours in seconds
        payload.put( "username", testee.getCaseNumber() );
        //payload.put( "role", "student" );
        payload.put( "nickname", testee.getLastName() );
        payload.put( "id", sessionId );      // proctoring session
        payload.put( "subject", exam.getName() + " - " + exam.getTestingPlan().getSubject().getTitle() ); // session name
        //payload.put( "api", "" );   // callback address

        String secretString = systemPropertyService.getProperty( StoredPropertyName.PROCTORING_SECRET_KEY );
        //SecretKey key = Keys.hmacShaKeyFor(secretString.getBytes( StandardCharsets.UTF_8 ));
        SignatureAlgorithm signAlgor = SignatureAlgorithm.HS256;

        Key key = new SecretKeySpec(secretString.getBytes( StandardCharsets.UTF_8 ), signAlgor.getJcaName());
        String jwt = Jwts.builder()
                .setClaims( payload )
                .signWith( key, signAlgor )
                .compact();

        return new StreamResponse() {
            @Override
            public String getContentType() {
                return "text/plain";
            }

            @Override
            public InputStream getStream() throws IOException {
                return new ByteArrayInputStream( jwt.getBytes( StandardCharsets.UTF_8 ) );
            }

            @Override
            public void prepareResponse( Response response ) {
                response.setContentLength( jwt.getBytes( StandardCharsets.UTF_8 ).length );
            }
        };
    }
}
