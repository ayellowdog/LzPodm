/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.service.service.detection.dhcp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inspur.podm.api.entity.ChassisEntity;
import com.inspur.podm.service.mapper.ChassisEntityMapper;

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
	private ChassisEntityMapper chassisEntityMapper;
	private static final Logger logger = LoggerFactory.getLogger(DhcpServiceDetector.class);
	public void test(String str) {
		logger.info("-------------this is test: "+ str + "----------------");
		List<ChassisEntity> list = chassisEntityMapper.getChassissById(1);
		System.out.println(list.size() + "&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
//		System.out.println(list.get(0).getModel() + "*******************************************");
		
//		try {
//			if (list.size() == 0) {
//				ChassisEntity chassis = new ChassisEntity();
//				chassis.setModel("nmb");
//				chassisEntityMapper.insertUseGeneratedKeys(chassis);
//				System.out.println("(((((((((((((((((((((((((((((");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		
	}
}

