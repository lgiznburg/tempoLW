package ru.rsmu.tempoLW.dao;

import ru.rsmu.tempoLW.entities.EmailQueue;

import java.util.List;

/**
 * @author leonid.
 */
public interface EmailDao extends BaseDao {
    List<EmailQueue> findNewEmails();
}
