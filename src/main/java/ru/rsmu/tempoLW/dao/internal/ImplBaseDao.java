package ru.rsmu.tempoLW.dao.internal;

import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.hibernate.Session;
import ru.rsmu.tempoLW.dao.BaseDao;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * @author leonid.
 */
@SuppressWarnings( "unchecked" )
public class ImplBaseDao implements BaseDao {
    @Inject
    protected Session session;

    public <T> T save(T entity) {
        session.saveOrUpdate( entity );
        return entity;
    }

    public <T, PK extends Serializable> T find(Class<T> type, PK id)
    {
        return (T) session.get(type, id);
    }

    public <T> List<T> findAll( Class<T> type ) {
        return session.createCriteria( type ).list();
    }

}
