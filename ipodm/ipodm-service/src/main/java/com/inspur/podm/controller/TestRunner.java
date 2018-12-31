/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.dao.ChassisDao;
import com.intel.podm.business.entities.redfish.Chassis;

/**
 * @ClassName: TestRunner
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年12月25日 上午9:24:49
 */
@Component
@Scope("prototype")
public class TestRunner implements Runnable{
@Autowired
private ChassisDao dao;

private String id;
	public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
	@Transactional
	@Override
	public void run() {
		Chassis c = dao.create();
		c.setName("hahaha");
		System.out.println("内层Thread：" + Thread.currentThread());
	}
	public ChassisDao getDao() {
		return dao;
	}
	public void setDao(ChassisDao dao) {
		this.dao = dao;
	}

}

