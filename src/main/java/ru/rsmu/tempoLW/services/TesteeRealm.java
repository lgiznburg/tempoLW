package ru.rsmu.tempoLW.services;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.apache.tapestry5.ioc.Messages;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.tempoLW.entities.ExamSchedule;
import ru.rsmu.tempoLW.entities.Testee;
import ru.rsmu.tempoLW.entities.auth.UserRoleName;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author leonid.
 */
public class TesteeRealm extends AuthorizingRealm {

    private final HibernateSessionManager sessionManager;
    private final Messages messages;

    public TesteeRealm( HibernateSessionManager sessionManager, Messages messages ) {
        super(new MemoryConstrainedCacheManager());
        this.sessionManager = sessionManager;
        this.messages = messages;
        setName("testeeAccounts");
        setAuthenticationTokenClass( UsernamePasswordToken.class);
        setCredentialsMatcher(new HashedCredentialsMatcher( Md5Hash.ALGORITHM_NAME));
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo( PrincipalCollection principals ) {
        if (principals == null) throw new AuthorizationException("PrincipalCollection was null, which should not happen");

        if (principals.isEmpty()) return null;

        if (principals.fromRealm(getName()).size() <= 0) return null;

        String login = (String) principals.fromRealm(getName()).iterator().next();
        if (login == null) return null;

        Set<String> roles = new HashSet<String>();
        roles.add( UserRoleName.testee.name() );
        return new SimpleAuthorizationInfo(roles);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo( AuthenticationToken token ) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;

        String login = upToken.getUsername();

        // Null login is invalid
        if (login == null) { throw new AccountException("Null usernames are not allowed by this realm."); }

        Testee testee = findByUsername(login);
        if ( testee == null ) {
            return null;
        }

        if ( !checkCurrentExam( testee ) ) {
            throw new LockedAccountException( messages.get( "realm.account-locked" ) ); //"Account [" + login + "] is locked."
        }
        if (testee.isCredentialsExpired()) {
            String msg = messages.get( "realm.account-expired" ); //"The credentials for account [" + login + "] are expired";
            throw new ExpiredCredentialsException(msg);
        }
        if ( ((UsernamePasswordToken) token).isRememberMe() ) {
            //remember me is not allowed for testees
            ((UsernamePasswordToken) token).setRememberMe( false );
        }
        return new SimpleAuthenticationInfo(login, testee.getPassword(), /*new SimpleByteSource(testee.getPasswordSalt()),*/ getName());
    }

    private Testee findByUsername( String login ) {
        Criteria criteria = sessionManager.getSession().createCriteria( Testee.class )
                .add( Restrictions.eq( "login", login ) );
        return (Testee) criteria.uniqueResult();
    }

    private boolean checkCurrentExam( Testee testee ) {
        Criteria criteria = sessionManager.getSession().createCriteria( ExamSchedule.class )
                .add( Restrictions.eq( "examDate", new Date() ) )
                .createAlias( "testees", "testee" )
                .add( Restrictions.eq( "testee.id", testee.getId() ) )
                .setMaxResults( 1 );
        return criteria.uniqueResult() != null;
    }

}
