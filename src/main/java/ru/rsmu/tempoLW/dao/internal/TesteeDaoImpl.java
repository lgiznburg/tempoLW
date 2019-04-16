package ru.rsmu.tempoLW.dao.internal;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.tempoLW.dao.TesteeDao;
import ru.rsmu.tempoLW.entities.Testee;

/**
 * @author leonid.
 */
public class TesteeDaoImpl extends BaseDaoImpl implements TesteeDao {
    @Override
    public Testee findByName( String testeeName ) {
        Criteria criteria = session.createCriteria( Testee.class )
                .add( Restrictions.eq( "login", testeeName ) );
        return (Testee) criteria.uniqueResult();

    }

    @Override
    public Testee findByCaseNumber( String caseNumber ) {
        Criteria criteria = session.createCriteria( Testee.class )
                .add( Restrictions.eq( "caseNumber", caseNumber ) );
        return (Testee) criteria.uniqueResult();
    }
}
