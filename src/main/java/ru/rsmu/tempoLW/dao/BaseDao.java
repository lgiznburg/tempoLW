package ru.rsmu.tempoLW.dao;

import org.apache.tapestry5.hibernate.annotations.CommitAfter;

import java.io.Serializable;
import java.util.List;

/**
 * @author leonid.
 */
public interface BaseDao {

    @CommitAfter
    <T> T save(T entity);

    <T, PK extends Serializable> T find(Class<T> type, PK id);
    <T> List<T> findAll( Class<T> type );
}
