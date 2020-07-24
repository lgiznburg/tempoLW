package ru.rsmu.tempoLW.services.impl;

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
import ru.rsmu.tempoLW.services.EmailService;
import ru.rsmu.tempoLW.services.EmailType;

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
            htmlEmail.setHostName( systemPropertyDao.getProperty( StoredPropertyName.SENDMAIL_HOST ) );
            String hostLogin = systemPropertyDao.getProperty( StoredPropertyName.SENDMAIL_LOGIN);
            String hostPassword = systemPropertyDao.getProperty( StoredPropertyName.SENDMAIL_PASSWORD);
            if ( StringUtils.isNotBlank( hostLogin ) && StringUtils.isNotBlank(hostPassword)) {
                htmlEmail.setAuthentication(hostLogin, hostPassword);
            }

            if (systemPropertyDao.getPropertyAsInt( StoredPropertyName.SENDMAIL_PORT ) > 0 )
                htmlEmail.setSmtpPort( systemPropertyDao.getPropertyAsInt( StoredPropertyName.SENDMAIL_PORT ) );

            htmlEmail.setStartTLSEnabled( systemPropertyDao.getPropertyAsInt( StoredPropertyName.SENDMAIL_USE_TLS ) > 0 );
            htmlEmail.setSSLOnConnect( systemPropertyDao.getPropertyAsInt( StoredPropertyName.SENDMAIL_USE_SSL ) > 0 );
            htmlEmail.setSslSmtpPort( systemPropertyDao.getProperty( StoredPropertyName.SENDMAIL_SSL_PORT) );

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
