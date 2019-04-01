package ru.rsmu.tempoLW.dao;

import ru.rsmu.tempoLW.entities.Testee;

/**
 * @author leonid.
 */
public interface TesteeDao extends BaseDao {
    Testee findByName( String testeeName );
}
