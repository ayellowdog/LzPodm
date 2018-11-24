/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.service.detection.dhcp;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inspur.podm.common.persistence.entity.Chassis;
import com.inspur.podm.service.dao.ChassisDao;
import com.inspur.podm.service.dao.MyChassis;
import com.inspur.podm.service.dao.MyChassisDao;

/**
 * @ClassName: DhcpServiceDetector
 * @Description: 通过DHCP发现设备的服务提供者.
 *
 * @author: liuchangbj
 * @date: 2018年11月14日 上午11:36:43
 */
@Service("dhcpServiceDetector")
public class DhcpServiceDetector {
	@Autowired
	ChassisDao chassisDao;
	@Autowired
	MyChassisDao mydao;
	private static final Logger logger = LoggerFactory.getLogger(DhcpServiceDetector.class);
	public void test(String str) {
		logger.info("-------------this is test: "+ str + "----------------");
		try {
//			MyChassis c = chassisDao.getOne((long) 1);
			MyChassis c = mydao.getChassisById((long) 1).get(0);
			System.out.println(c.getChassisType());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

