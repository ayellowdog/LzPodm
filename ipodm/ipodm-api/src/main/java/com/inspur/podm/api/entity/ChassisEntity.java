/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.api.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.inspur.podm.common.persistence.BaseEntity;

/**
 * @ClassName: ChassisEntity
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年11月16日 上午11:08:41
 */
@Table(name = "chassis")
public class ChassisEntity extends BaseEntity {

	/** @Fields serialVersionUID: TODO 功能描述  */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	private int id;
	
	private String model;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

}

