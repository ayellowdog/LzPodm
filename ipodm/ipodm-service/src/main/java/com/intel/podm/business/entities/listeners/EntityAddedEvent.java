/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.intel.podm.business.entities.listeners;

import org.springframework.context.ApplicationEvent;

import com.intel.podm.business.entities.redfish.base.Entity;

/**
 * @ClassName: EntityAddedEvent
 * @Description: 自己封装的EntityAddedEvent
 *
 * @author: liuchangbj
 * @date: 2019年1月3日 下午2:33:48
 */
public class EntityAddedEvent extends ApplicationEvent{
	/** @Fields serialVersionUID: TODO 功能描述  */
	private static final long serialVersionUID = 2950922861869640695L;
	
	private Entity entity;

	public EntityAddedEvent(Object source, Entity entity) {
		super(source);
		this.entity = entity;
	}
	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}
}

