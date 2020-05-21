package ru.rsmu.tempoLW.pages.admin;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import ru.rsmu.tempoLW.dao.SystemPropertyDao;
import ru.rsmu.tempoLW.entities.system.StoredProperty;
import ru.rsmu.tempoLW.entities.system.StoredPropertyName;

import java.util.*;

/**
 * @author leonid.
 */
@Import( module = "bootstrap/collapse" )
public class SystemProperties {
    @Property
    private List<StoredProperty> propertiesList;

    /**
     * Property for interaction through properties list
     */
    private StoredProperty storedProperty;

    private boolean isSubmission;

    @Property
    private StoredProperty selectedProperty;

    /**
     * Store group name to show only group for first element only
     */
    private String currentGroup = "";

    /**
     * Copies of updated values.
     */
    private List<StoredProperty> valuesCopy;

    @Inject
    private SystemPropertyDao propertyService;

    public void onPrepareForRender() {
        isSubmission = false;
        prepare();
    }
    public void onPrepareForSubmit() {
        isSubmission = true;
        prepare();
        valuesCopy = new ArrayList<>();
    }

    private void prepare() {
        Map<StoredPropertyName,StoredProperty> properties = new HashMap<StoredPropertyName, StoredProperty>();
        for ( StoredProperty property : propertyService.findAll( StoredProperty.class ) ) {
            properties.put( property.getPropertyName(), property );
        }
        for ( StoredPropertyName names : StoredPropertyName.values() ) {
            if ( !properties.containsKey( names ) ) {
                properties.put( names, new StoredProperty( names, names.getDefaultValue() ) );
            }
        }
        propertiesList = new LinkedList<StoredProperty>( properties.values() );
        Collections.sort( propertiesList );
        currentGroup = "";
    }

    public String getGroupName() {
        if ( !currentGroup.equals( storedProperty.getPropertyName().getGroupName() ) ) {
            currentGroup = storedProperty.getPropertyName().getGroupName();
            return currentGroup;
        } else {
            return "";
        }
    }

    public ValueEncoder<StoredProperty> getPropertiesEncoder() {
        return new ValueEncoder<StoredProperty>() {
            @Override
            public String toClient( StoredProperty value ) {
                return value.getPropertyName().name();
            }

            @Override
            public StoredProperty toValue( String clientValue ) {
                StoredPropertyName propertyName = StoredPropertyName.valueOf( clientValue );
                return propertyService.find( propertyName );
            }
        };
    }

    //void onValidateFromPropertyValue( String value ) {
    //    valuesCopy.put( storedProperty.getPropertyName(), storedProperty.getValue() );
    //}

    void onSuccess() {
        for ( StoredProperty property : valuesCopy ) {
            if ( StringUtils.isBlank( property.getValue() ) ) {
                continue;
            }
            if ( property.getValue().equals( property.getPropertyName().getDefaultValue() )) {
                if ( property.getId() > 0 ) {
                    propertyService.delete( property );
                }
                continue;
            }
            propertyService.saveProperty( property );
        }
    }

    public StoredProperty getStoredProperty() {
        return storedProperty;
    }

    public void setStoredProperty( StoredProperty storedProperty ) {
        this.storedProperty = storedProperty;
        if ( isSubmission ) {
            valuesCopy.add( storedProperty );
        }
    }
}
