package ru.rsmu.tempoLW.dao.internal;

import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.hibernate.Hibernate;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import ru.rsmu.tempoLW.dao.BaseDao;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * @author leonid.
 */
@SuppressWarnings( "unchecked" )
public class BaseDaoImpl implements BaseDao {
    @Inject
    protected Session session;

    public <T> T save(T entity) {
        try {
            session.saveOrUpdate( entity );
        } catch (NonUniqueObjectException e) {
            session.merge( entity );
        }
        return entity;
    }

    public <T, PK extends Serializable> T find(Class<T> type, PK id)
    {
        return (T) session.get(type, id);
    }

    public <T> List<T> findAll( Class<T> type ) {
        return session.createCriteria( type ).list();
    }

    @Override
    public <T> void delete( T entity ) {
        session.delete( entity );
    }

    @Override
    public <T> void refresh( T entity ) {
        session.refresh( entity );
    }

    @Override
    public <T> void softInitialize( T entity ) {
        if ( ! Hibernate.isInitialized( entity ) ) {
            Hibernate.initialize( entity );
        }
    }

}
