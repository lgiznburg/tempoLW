package ru.rsmu.tempoLW.dao;

import ru.rsmu.tempoLW.entities.auth.SubjectManager;
import ru.rsmu.tempoLW.entities.auth.User;
import ru.rsmu.tempoLW.entities.auth.UserRoleName;

import java.util.List;

/**
 * @author leonid.
 */
public interface UserDao extends BaseDao {
    User findByUsername( String username );
    SubjectManager findSubjectsForUser( User user );

    String encrypt( String password );

    List<User> finUserForRole( UserRoleName roleName );
}
