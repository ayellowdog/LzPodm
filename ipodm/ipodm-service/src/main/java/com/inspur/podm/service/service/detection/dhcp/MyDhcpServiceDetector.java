/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.service.detection.dhcp;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.common.context.AppContext;
import com.intel.podm.business.entities.dao.ChassisDao;
import com.intel.podm.business.entities.dao.MyChassisDao;
import com.intel.podm.business.entities.redfish.Chassis;
import com.intel.podm.common.types.Id;
import com.intel.podm.discovery.external.ExternalServiceMonitoringEvent;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
/**
 * @ClassName: DhcpServiceDetector
 * @Description: 通过DHCP发现设备的服务提供者.
 *
 * @author: liuchangbj
 * @date: 2018年11月14日 上午11:36:43
 */
@Service("myDhcpServiceDetector")
public class MyDhcpServiceDetector implements MyDhcpServiceDetectorInterface {
	@Autowired
	ChassisDao chassisDao;
	@Autowired
	MyChassisDao myDao;
	private static final Logger logger = LoggerFactory.getLogger(MyDhcpServiceDetector.class);
	@Override
	@Transactional
	public void test(String str) throws Exception {
		System.out.println("。。。。。开始测试了。。。。。。。。。。");
			Chassis chassis = chassisDao.create();
			chassis.setDescription("eventTest");
			AppContext.context().publishEvent(ExternalServiceMonitoringEvent.externalServiceMonitoringStartedEvent(this,UUID.randomUUID()));
		throw new RuntimeException("hehe");
	}

}

