package ru.rsmu.tempoLW.services;

import ru.rsmu.tempoLW.entities.Testee;

import java.util.Map;

/**
 * @author leonid.
 */
public interface EmailService {
    void sendEmail( Testee user, EmailType emailType, Map<String,Object> model );
    void sendEmail( String to, EmailType emailType, Map<String,Object> model );
}
