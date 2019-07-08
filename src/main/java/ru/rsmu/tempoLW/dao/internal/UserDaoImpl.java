package ru.rsmu.tempoLW.dao.internal;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.tempoLW.dao.UserDao;
import ru.rsmu.tempoLW.entities.auth.SubjectManager;
import ru.rsmu.tempoLW.entities.auth.User;
import ru.rsmu.tempoLW.utils.PasswordEncoder;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author leonid.
 */
public class UserDaoImpl extends BaseDaoImpl implements UserDao {
    @Override
    public User findByUsername( String username ) {
        Criteria criteria = session.createCriteria( User.class )
                .add( Restrictions.eq( "username", username ) )
                .setMaxResults( 1 );
        return (User) criteria.uniqueResult();
    }

    @Override
    public SubjectManager findSubjectsForUser( User user ) {
        Criteria criteria = session.createCriteria( SubjectManager.class )
                .add( Restrictions.eq( "user", user ) )
                .setMaxResults( 1 );
        return (SubjectManager) criteria.uniqueResult();
    }

    @Override
    public String encrypt( String password ) {
        try {
            return PasswordEncoder.encrypt( password );
        } catch ( NoSuchAlgorithmException e ) {
            throw new RuntimeException( e );
        }
    }

}
