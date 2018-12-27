package com.intel.podm.business.entities.redfish.base;

import static java.lang.String.format;
import static java.util.Objects.hash;
import static javax.persistence.GenerationType.IDENTITY;

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreRemove;
import javax.persistence.Version;

import com.intel.podm.business.entities.listeners.EntityListenerImpl;


/**
 * 
 * @ClassName: BaseEntity
 * @Description: TODO 功能描述
 *
 * @author: liuchangbj
 * @date: 2018年11月19日 下午4:02:07
 */
@MappedSuperclass
@EntityListeners(EntityListenerImpl.class)
public abstract class Entity {
    protected static final String ENTITY_ID_NUMERIC_COLUMN_DEFINITION = "bigserial";
    protected static final String ENTITY_ID_STRING_COLUMN_DEFINITION = "text";

    @javax.persistence.Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private long id;

	@Version
    @Column(name = "version", columnDefinition = "integer DEFAULT 0", nullable = false)
    private long version;

    @Column(name = "event_source_context")
    private URI eventSourceContext;

    protected long getPrimaryKey() {
        return id;
    }

    public abstract void preRemove();

    public abstract boolean containedBy(Entity possibleParent);

    @PreRemove
    public void unlinkRelations() {
        preRemove();
    }

    protected boolean isContainedBy(Entity possibleParent, Entity realParent) {
        return possibleParent != null && Objects.equals(realParent, possibleParent);
    }

    protected boolean isContainedBy(Entity possibleParent, Collection<? extends Entity> realParents) {
        if (possibleParent == null || realParents == null) {
            return false;
        }

        return realParents.stream().filter(realParent -> isContainedBy(possibleParent, realParent)).count() > 0;
    }

    protected <T extends Entity> void unlinkCollection(Collection<T> entities, Consumer<T> unlinkConsumer, Predicate<T> predicate) {
        // Iterator prevents ConcurrentModification exception, update method carefully. Checked by unit test.
        Iterator<T> iterator = entities.iterator();
        while (iterator.hasNext()) {
            T entity = iterator.next();
            if (predicate.test(entity)) {
                unlinkConsumer.accept(entity);
                iterator = entities.iterator();
            }
        }
    }

    protected <T extends Entity> void unlinkCollection(Collection<T> entities, Consumer<T> unlinkConsumer) {
        unlinkCollection(entities, unlinkConsumer, x -> true);
    }

    @Override
    public int hashCode() {
        return hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || (!(o instanceof Entity))) {
            return false;
        }
        Entity that = (Entity) o;
        return Objects.equals(getPrimaryKey(), that.getPrimaryKey());
    }

    @Override
    public String toString() {
        return format("Entity {clazz=%s, primaryKey=%d}", getClass().getSimpleName(), getPrimaryKey());
    }

    public URI getEventSourceContext() {
        return eventSourceContext;
    }

    public void setEventSourceContext(URI context) {
        this.eventSourceContext = context;
    }
}
