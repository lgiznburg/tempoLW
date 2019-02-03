package ru.rsmu.tempoLW.dao;

import ru.rsmu.tempoLW.entities.auth.SubjectManager;
import ru.rsmu.tempoLW.entities.auth.User;

/**
 * @author leonid.
 */
public interface UserDao extends BaseDao {
    User findByUsername( String username );
    SubjectManager findSubjectsForUser( User user );

    String encrypt( String password );
}
