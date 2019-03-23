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
import org.apache.shiro.util.SimpleByteSource;
import org.apache.tapestry5.hibernate.HibernateSessionManager;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.tempoLW.entities.auth.User;
import ru.rsmu.tempoLW.entities.auth.UserRole;

import java.util.HashSet;
import java.util.Set;

/**
 * @author leonid.
 */
public class UserDetailsRealm extends AuthorizingRealm {

    private final HibernateSessionManager sessionManager;

    public UserDetailsRealm( HibernateSessionManager sessionManager ) {
        super(new MemoryConstrainedCacheManager());
        this.sessionManager = sessionManager;
        setName("adminAccounts");
        setAuthenticationTokenClass(UsernamePasswordToken.class);
        setCredentialsMatcher(new HashedCredentialsMatcher( Md5Hash.ALGORITHM_NAME));
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo( PrincipalCollection principals ) {
        if (principals == null) throw new AuthorizationException("PrincipalCollection was null, which should not happen");

        if (principals.isEmpty()) return null;

        if (principals.fromRealm(getName()).size() <= 0) return null;

        String username = (String) principals.fromRealm(getName()).iterator().next();
        if (username == null) return null;
        User user = findByUsername(username);
        if (user == null) return null;
        Set<String> roles = new HashSet<String>(user.getRoles().size());
        for (UserRole role : user.getRoles())
            roles.add(role.getRoleName().name());
        return new SimpleAuthorizationInfo(roles);
    }

    private User findByUsername( String username ) {
        Criteria criteria = sessionManager.getSession().createCriteria( User.class )
                .add( Restrictions.eq( "username", username ) );
        return (User) criteria.uniqueResult();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo( AuthenticationToken token ) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;

        String username = upToken.getUsername();

        // Null username is invalid
        if (username == null) { throw new AccountException("Null usernames are not allowed by this realm."); }

        User user = findByUsername(username);
        if ( user == null ) {
            return null;
        }

        if ( !user.isEnabled() ) { throw new LockedAccountException("Account [" + username + "] is locked."); }
        /*iif (user.isCredentialsExpired()) {
            String msg = "The credentials for account [" + username + "] are expired";
            throw new ExpiredCredentialsException(msg);
        }*/
        return new SimpleAuthenticationInfo(username, user.getPassword(), /*new SimpleByteSource(user.getPasswordSalt()),*/ getName());
    }
}
