package ru.rsmu.tempoLW.dao;

import ru.rsmu.tempoLW.entities.auth.User;

/**
 * @author leonid.
 */
public interface UserDao extends BaseDao {
    User findByUsername( String username );
}
