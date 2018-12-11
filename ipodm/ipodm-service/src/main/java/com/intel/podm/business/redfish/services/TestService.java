/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.intel.podm.business.redfish.services;

import static com.intel.podm.common.types.ChassisType.POD;
import static com.intel.podm.common.utils.IterableHelper.singleOrNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.dao.ChassisDao;
import com.intel.podm.business.entities.redfish.Chassis;
import com.intel.podm.common.types.ChassisType;

/**
 * @ClassName: TestService
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年12月11日 上午10:23:18
 */
@Service
public class TestService {
	
	@Autowired
	private ChassisDao chassisDao;

	@Transactional(propagation = Propagation.REQUIRED)
    public void test() {
        Chassis podChassis = singleOrNull(chassisDao.getAllByChassisType(POD));
        //为了方便调试，暂时注掉
//        podManagerDiscoveryHandler.getManagerForPod(podChassis);
    }
}

