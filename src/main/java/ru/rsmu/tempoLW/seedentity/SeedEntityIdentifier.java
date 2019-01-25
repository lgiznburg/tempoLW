package ru.rsmu.tempoLW.seedentity;

/**
 * copied from tynamo-hibernate-seedentity because of Hibernate version incompatible
 */
public final class SeedEntityIdentifier {
    private Object entity;
    private String uniquelyIdentifyingProperty;

    public SeedEntityIdentifier(Object entity, String uniquelyIdentifyingProperty) {
        this.entity = entity;
        this.uniquelyIdentifyingProperty = uniquelyIdentifyingProperty;
    }

    public Object getEntity() {
        return entity;
    }

    public String getUniquelyIdentifyingProperty() {
        return uniquelyIdentifyingProperty;
    }

}
