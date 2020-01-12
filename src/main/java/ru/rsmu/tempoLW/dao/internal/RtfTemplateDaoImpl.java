package ru.rsmu.tempoLW.dao.internal;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.tempoLW.dao.RtfTemplateDao;
import ru.rsmu.tempoLW.entities.DocumentTemplate;
import ru.rsmu.tempoLW.entities.DocumentTemplateType;

/**
 * @author leonid.
 */
public class RtfTemplateDaoImpl extends BaseDaoImpl implements RtfTemplateDao {
    @Override
    public DocumentTemplate findByType( DocumentTemplateType type ) {
        Criteria criteria = session.createCriteria( DocumentTemplate.class )
                .add( Restrictions.eq( "templateType", type ) )
                .setMaxResults( 1 );
        return (DocumentTemplate) criteria.uniqueResult();
    }
}
