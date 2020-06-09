package ru.rsmu.tempoLW.services;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rsmu.tempoLW.dao.SystemPropertyDao;
import ru.rsmu.tempoLW.entities.Testee;
import ru.rsmu.tempoLW.entities.system.StoredPropertyName;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author leonid.
 */
public class EmailServiceImpl implements EmailService {

        private static final Logger log = LoggerFactory.getLogger( EmailService.class );

        // parameters for using with commons email

//        @Value("${emailService.hostName:127.0.0.1}")
        private String hostName;
//        @Value("${emailService.hostLogin:login}")
        private String hostLogin;
//        @Value("${emailService.hostPassword:password}")
        private String hostPassword;
//        @Value("${emailService.hostPort:0}")
        private int hostPort = 0;
//        @Value("${emailService.hostSslPort:}")
        private String hostSslPort = "";
//        @Value("${emailService.useSsl:false}")
        private boolean useSsl = false;
//        @Value("${emailService.useTls:false}")
        private boolean useTls = false;


        private boolean debug = false;

        private VelocityEngine velocityEngine;

        @Inject
        private SystemPropertyDao systemPropertyDao;

    public EmailServiceImpl() {
        velocityEngine = new VelocityEngine();
        try {
            Properties velocityProp = new Properties();
            InputStream velocityFile = getClass().getResourceAsStream( "/velocity.properties" );
            velocityProp.load( velocityFile );
            velocityEngine.init( velocityProp );
        } catch (Exception e) {
            log.error( "Can't open velocity.properties file", e );
        }

        try {
            Properties prop = new Properties();

            InputStream propFile = getClass().getResourceAsStream( "/email_service.properties" );
            prop.load( propFile );

            hostName = StringUtils.isNotBlank( prop.getProperty( "hostName" ) ) ? prop.getProperty( "hostName" ) : "127.0.0.1" ;
            hostLogin = StringUtils.isNotBlank( prop.getProperty( "hostLogin" ) ) ? prop.getProperty( "hostLogin" ) : "login";
            hostPassword = StringUtils.isNotBlank( prop.getProperty( "hostPassword" ) ) ? prop.getProperty( "hostPassword" ) : "password";
            hostPort = StringUtils.isNotBlank( prop.getProperty( "hostPort" ) ) ? Integer.parseInt( prop.getProperty( "hostPort" ) ) : 0;
            hostSslPort  = StringUtils.isNotBlank( prop.getProperty( "hostSslPort" ) ) ? prop.getProperty( "hostSslPort" ) : "";
            useSsl = StringUtils.isNotBlank( prop.getProperty( "useSsl" ) ) && Boolean.parseBoolean( prop.getProperty( "useSsl" ) );
            useTls = StringUtils.isNotBlank( prop.getProperty( "useTls" ) ) && Boolean.parseBoolean( prop.getProperty( "useTls" ) );

        } catch (IOException e) {
            log.error( "Can't open email_service.properties file", e );
        }
    }

    public void sendEmail( Testee user, EmailType emailType, Map<String,Object> model ) {
            try {
                HtmlEmail email = createHtmlEmail( emailType, model );
                email.addTo( user.getEmail(), user.getFullName() );
                email.send();

            } catch (EmailException e) {
                log.error( String.format( "Email to %s (%s) wasn't sent", user.getEmail(), user.getCaseNumber() ), e );
            }
        }

        public void sendEmail( String to, EmailType emailType, Map<String,Object> model ) {
            try {

                HtmlEmail email = createHtmlEmail( emailType, model );
                email.addTo( to );
                email.send();

            } catch (EmailException e) {
                log.error( String.format( "Email to %s wasn't sent", to ), e );
            }
        }


        private HtmlEmail createHtmlEmail( EmailType emailType, Map<String,Object> model) throws EmailException {
            final HtmlEmail htmlEmail = new HtmlEmail();
            htmlEmail.setHostName(hostName);
            if ( StringUtils.isNotBlank( hostLogin ) && StringUtils.isNotBlank(hostPassword)) {
                htmlEmail.setAuthentication(hostLogin, hostPassword);
            }

            if (hostPort > 0)
                htmlEmail.setSmtpPort(hostPort);

            htmlEmail.setStartTLSEnabled( useTls );
            htmlEmail.setSSLOnConnect( useSsl );
            htmlEmail.setSslSmtpPort( hostSslPort );

            htmlEmail.setFrom( systemPropertyDao.getProperty( StoredPropertyName.EMAIL_FROM_ADDRESS ),
                    systemPropertyDao.getProperty( StoredPropertyName.EMAIL_FROM_SIGNATURE ),
                    "UTF-8" );

            try {
                List<InternetAddress> replyToAddresses = new ArrayList<>();
                replyToAddresses.add( new InternetAddress("noreply@rsmu.ru") );
                htmlEmail.setReplyTo( replyToAddresses );
            } catch (AddressException e) {
                // what?
            }

            model.put( "replyEmail", systemPropertyDao.getProperty( StoredPropertyName.EMAIL_FROM_ADDRESS ) );
            model.put( "signature", systemPropertyDao.getProperty( StoredPropertyName.EMAIL_FROM_SIGNATURE ) );
            htmlEmail.setSubject( emailType.getSubject() );
            htmlEmail.setHtmlMsg( generateEmailMessage( emailType.getFileName(), model ) );

            return htmlEmail;
        }

        private String generateEmailMessage(final String template, final Map<String,Object> model) throws EmailException {

            try {
                final StringWriter message = new StringWriter();
                final ToolManager toolManager = new ToolManager();
                final ToolContext toolContext = toolManager.createContext();
                final VelocityContext context = new VelocityContext(model, toolContext);

                velocityEngine.mergeTemplate( template, "UTF-8", context, message );
                return message.getBuffer().toString();

            } catch (Exception e) {
                throw new EmailException("Can't create email body", e);
            }
        }

}
