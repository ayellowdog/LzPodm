/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.service.detection.dhcp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.dao.ChassisDao;
import com.intel.podm.business.entities.dao.MyChassisDao;
import com.intel.podm.business.entities.redfish.Chassis;
import com.intel.podm.common.types.Id;

/**
 * @ClassName: DhcpServiceDetector
 * @Description: 通过DHCP发现设备的服务提供者.
 *
 * @author: liuchangbj
 * @date: 2018年11月14日 上午11:36:43
 */
@Service("dhcpServiceDetector")
public class DhcpServiceDetector implements DhcpServiceDetectorInterface {
	@Autowired
	ChassisDao chassisDao;
	@Autowired
	MyChassisDao myDao;
	private static final Logger logger = LoggerFactory.getLogger(DhcpServiceDetector.class);
	@Override
	@Transactional
	public void test(String str) {
		logger.info("-------------this is test: "+ str + "----------------");
		try {
			Chassis chassis = chassisDao.create();
			chassis.setDescription("123456567");
			
////			MyChassis c = chassisDao.getOne((long) 1);
//			MyChassis c = mydao.getChassisById((long) 1).get(0);
//			System.out.println(c.getAssetTag());
//			chassisDao.create();
			Chassis c = new Chassis();
			c.setDescription("dasdasdasdasd");
			myDao.save(c);
//			System.out.println("结束了——————————————"+ a.getDescription());
//			List<Id> list = chassisDao.findAllChassisFromPrimaryDataSource();
//			System.out.println("list size is " + list.size());
		} catch (Exception e) {
			System.out.println("出错了——————————————");
			e.printStackTrace();
		}
	}
}

