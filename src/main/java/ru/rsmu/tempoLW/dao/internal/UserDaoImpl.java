package ru.rsmu.tempoLW.dao.internal;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.tempoLW.dao.UserDao;
import ru.rsmu.tempoLW.entities.auth.SubjectManager;
import ru.rsmu.tempoLW.entities.auth.User;
import ru.rsmu.tempoLW.entities.auth.UserRoleName;
import ru.rsmu.tempoLW.utils.PasswordEncoder;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author leonid.
 */
@SuppressWarnings( "unchecked" )
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

    @Override
    public List<User> finUserForRole( UserRoleName roleName ) {
        Criteria criteria = session.createCriteria( User.class )
                .createAlias("roles", "roles"  )
                .add( Restrictions.eq( "roles.roleName", roleName ) )
                .setResultTransformer( CriteriaSpecification.DISTINCT_ROOT_ENTITY );
        return criteria.list();
    }
}
