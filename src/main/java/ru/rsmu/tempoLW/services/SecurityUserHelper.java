package ru.rsmu.tempoLW.services;

import org.apache.shiro.subject.Subject;
import org.tynamo.security.services.SecurityService;
import ru.rsmu.tempoLW.dao.TesteeDao;
import ru.rsmu.tempoLW.dao.UserDao;
import ru.rsmu.tempoLW.entities.Testee;
import ru.rsmu.tempoLW.entities.auth.SubjectManager;
import ru.rsmu.tempoLW.entities.auth.User;
import ru.rsmu.tempoLW.entities.auth.UserRoleName;

/**
 * @author leonid.
 */
public class SecurityUserHelper {

    private SecurityService securityService;

    private UserDao userDao;

    private TesteeDao testeeDao;

    public SecurityUserHelper( SecurityService securityService, UserDao userDao, TesteeDao testeeDao ) {
        this.securityService = securityService;
        this.userDao = userDao;
        this.testeeDao = testeeDao;
    }

    public User getCurrentUser() {
        if ( !securityService.isUser() || securityService.hasRole( UserRoleName.testee.name() ) ) {
            return null; // no user
        }
        String username = (String) securityService.getSubject().getPrincipal();
        User user = userDao.findByUsername( username );
        return user;
    }

    public SubjectManager getSubjectManager( User user ) {
        return userDao.findSubjectsForUser( user );
    }

    public Testee getCurrentTestee() {
        if ( !securityService.isUser() || !securityService.hasRole( UserRoleName.testee.name() ) ) {
            return null;   // no testee
        }
        String testeeName = (String) securityService.getSubject().getPrincipal();
        Testee testee = testeeDao.findByName( testeeName );
        return testee;
    }
}
