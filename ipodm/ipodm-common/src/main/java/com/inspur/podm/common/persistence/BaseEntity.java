package com.inspur.podm.common.persistence;

import static java.lang.String.format;
import static java.util.Objects.hash;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.persistence.PreRemove;

/**
 * 
 * @ClassName: BaseEntity
 * @Description: TODO 功能描述
 *
 * @author: liuchangbj
 * @date: 2018年11月19日 下午4:02:07
 */
public abstract class BaseEntity implements Serializable{
    protected static final String ENTITY_ID_NUMERIC_COLUMN_DEFINITION = "bigserial";
    protected static final String ENTITY_ID_STRING_COLUMN_DEFINITION = "text";
    /**
     * 序列号.
     */
    private static final long serialVersionUID = 1L;
//	@Id
//	@Column(name = "id")
	private long id;
	
	/**
	 * RSD中用于persist的插入验证，后续优化.
	 */
//    @Column(name = "version")
    private long version;
	
//    @Column(name = "event_source_context")
    private URI eventSourceContext;
    
    protected long getPrimaryKey() {
        return id;
    }

    public abstract void preRemove();

    public abstract boolean containedBy(BaseEntity possibleParent);

    @PreRemove
    public void unlinkRelations() {
        preRemove();
    }

    protected boolean isContainedBy(BaseEntity possibleParent, BaseEntity realParent) {
        return possibleParent != null && Objects.equals(realParent, possibleParent);
    }

    protected boolean isContainedBy(BaseEntity possibleParent, Collection<? extends BaseEntity> realParents) {
        if (possibleParent == null || realParents == null) {
            return false;
        }

        return realParents.stream().filter(realParent -> isContainedBy(possibleParent, realParent)).count() > 0;
    }

    protected <T extends BaseEntity> void unlinkCollection(Collection<T> entities, Consumer<T> unlinkConsumer, Predicate<T> predicate) {
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

    protected <T extends BaseEntity> void unlinkCollection(Collection<T> entities, Consumer<T> unlinkConsumer) {
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
        if (o == null || (!(o instanceof BaseEntity))) {
            return false;
        }
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(getPrimaryKey(), that.getPrimaryKey());
    }

    @Override
    public String toString() {
        return format("Entity {clazz=%s, primaryKey=%d}", getClass().getSimpleName(), getPrimaryKey());
    }
    
//    public long getId() {
//		return id;
//	}
//
//	public void setId(long id) {
//		this.id = id;
//	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public URI getEventSourceContext() {
        return eventSourceContext;
    }

    public void setEventSourceContext(URI context) {
        this.eventSourceContext = context;
    }
}
