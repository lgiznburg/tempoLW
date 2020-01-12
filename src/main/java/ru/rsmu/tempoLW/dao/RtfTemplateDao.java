package ru.rsmu.tempoLW.dao;

import ru.rsmu.tempoLW.entities.DocumentTemplate;
import ru.rsmu.tempoLW.entities.DocumentTemplateType;

/**
 * @author leonid.
 */
public interface RtfTemplateDao extends BaseDao {
    DocumentTemplate findByType( DocumentTemplateType type );
}
