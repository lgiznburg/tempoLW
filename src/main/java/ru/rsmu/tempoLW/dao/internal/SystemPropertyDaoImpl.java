package ru.rsmu.tempoLW.dao.internal;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import ru.rsmu.tempoLW.dao.SystemPropertyDao;
import ru.rsmu.tempoLW.entities.system.StoredProperty;
import ru.rsmu.tempoLW.entities.system.StoredPropertyName;
import ru.rsmu.tempoLW.entities.system.StoredPropertyType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author leonid.
 */
public class SystemPropertyDaoImpl extends BaseDaoImpl implements SystemPropertyDao {

    static final private SimpleDateFormat dateFormat = new SimpleDateFormat( "dd-MM-yyyy" );
    static final private SimpleDateFormat timeFormat = new SimpleDateFormat( "hh:mm" );


    private HashMap<StoredPropertyName, String> properties = new HashMap<>();


    private void buildPropertiesMap() {
        List<StoredProperty> storedProperties = super.findAll( StoredProperty.class );
        for ( StoredProperty property : storedProperties ) {
            properties.put( property.getPropertyName(), property.getValue() );
        }
    }

    public String getProperty( StoredPropertyName propertyName ) {
        if ( properties.isEmpty() ) {
            buildPropertiesMap();
        }
        String propertyValue = properties.get( propertyName );
        return propertyValue != null ? propertyValue : propertyName.getDefaultValue();
    }

    public Integer getPropertyAsInt( StoredPropertyName propertyName ) {
        final String prop = this.getProperty( propertyName );
        try {
            return Integer.parseInt( prop );
        } catch (NumberFormatException e) {
            try {  //protection against foolish user
                return Integer.parseInt( propertyName.getDefaultValue() );
            } catch (NumberFormatException e2) {
                return null; // this is fatal
            }
        }
    }

    public Long getPropertyAsLong( StoredPropertyName propertyName ) {
        final String prop = this.getProperty( propertyName );
        try {
            return Long.parseLong( prop );
        } catch (NumberFormatException e) {
            try {  //protection against foolish user
                return Long.parseLong( propertyName.getDefaultValue() );
            } catch (NumberFormatException e2) {
                return null; // this is fatal
            }
        }
    }

    public Date getPropertyAsDate( StoredPropertyName name ) {
        if ( !name.getType().equals( StoredPropertyType.DATE )
                && !name.getType().equals( StoredPropertyType.TIME ) ) {
            return null;
        }
        String value = getProperty( name );
        Date result = null;
        try {
            switch ( name.getType() ) {
                case DATE :
                    result = dateFormat.parse( value );
                    break;
                case TIME:
                    result = timeFormat.parse( value );
                    break;
                default:
            }
        } catch (ParseException e) {
            //nothing, return null
        }

        return result;
    }


    public void saveProperty( StoredProperty property ) {
        super.save( property );
        properties.put( property.getPropertyName(), property.getValue() );
    }

    @Override
    public StoredProperty find( StoredPropertyName propertyName ) {
        Criteria criteria = session.createCriteria( StoredProperty.class )
                .add( Restrictions.eq( "propertyName", propertyName ) )
                .setMaxResults( 1 );
        StoredProperty result = (StoredProperty) criteria.uniqueResult();
        if ( result == null ) {
            // this properties is not stored yet. Create a new one
            return new StoredProperty( propertyName, propertyName.getDefaultValue() );
        }
        return result;
    }

    public void delete( StoredProperty entity ) {
        properties.remove( entity.getPropertyName() );
        super.delete( entity );
    }
}
