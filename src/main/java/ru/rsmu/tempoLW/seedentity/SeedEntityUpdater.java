package ru.rsmu.tempoLW.seedentity;

/**
 * copied from tynamo-hibernate-seedentity because of Hibernate version incompatible
 */
public final class SeedEntityUpdater {
    private Object originalEntity;
    private Object updatedEntity;
    private boolean forceUpdate;

    public SeedEntityUpdater(Object originalEntity, Object updatedEntity) {
        this(originalEntity, updatedEntity, false);
    }

    public SeedEntityUpdater(Object originalEntity, Object updatedEntity, boolean forceUpdate) {
        this.originalEntity = originalEntity;
        this.updatedEntity = updatedEntity;
        this.forceUpdate = forceUpdate;
    }

    public Object getOriginalEntity() {
        return originalEntity;
    }

    public Object getUpdatedEntity() {
        return updatedEntity;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

}
