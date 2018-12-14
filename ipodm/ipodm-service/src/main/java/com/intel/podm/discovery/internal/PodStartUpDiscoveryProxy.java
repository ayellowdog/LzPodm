/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.intel.podm.discovery.internal;

import static com.intel.podm.common.types.ChassisType.POD;
import static com.intel.podm.common.types.Health.OK;
import static com.intel.podm.common.types.Id.id;
import static com.intel.podm.common.types.State.ENABLED;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;

import com.intel.podm.business.entities.dao.ChassisDao;
import com.intel.podm.business.entities.redfish.Chassis;
import com.intel.podm.common.types.Status;
import com.intel.podm.config.base.Config;
import com.intel.podm.config.base.ConfigProvider;
import com.intel.podm.config.base.dto.DiscoveryConfig;
import static com.intel.podm.common.utils.IterableHelper.singleOrNull;

/**
 * @ClassName: PodStartUpDiscoveryProxy
 * @Description: 因为在spring框架中， PostConstruct注解的方法不支持事务注解，因此封装了这个对象，实现事务。
 * 具体原因是Spring的事务基于AOP实现，而PostConstruct标注的方法执行时，可能还不符合AOP的条件。
 *
 * @author: liuchangbj
 * @date: 2018年12月11日 上午11:03:04
 */
@Component
public class PodStartUpDiscoveryProxy {
    @Autowired
    private ChassisDao chassisDao;
	@Config
	@Resource(name="podmConfigProvider")
	private ConfigProvider discoveryConfig;
    @Autowired
    private PodManagerDiscoveryHandler podManagerDiscoveryHandler;
    private static final Logger logger = LoggerFactory.getLogger(PodStartUpDiscoveryProxy.class);
//  @Lock(WRITE)
//  @Transactional(REQUIRED)
//  @AccessTimeout(value = 5, unit = SECONDS)
    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRED, timeout = 5)
    public void initInitialPod() {
        Chassis podChassis = singleOrNull(chassisDao.getAllByChassisType(POD));
        if (podChassis == null) {
            String podLocation = discoveryConfig.get(DiscoveryConfig.class).getPodLocationId();
            logger.info("Creating POD at location {}", podLocation);
            podChassis = createPod(podLocation);
        }
        podManagerDiscoveryHandler.getManagerForPod(podChassis);
    }
	
    public Chassis createPod(String podLocation) {
        Chassis pod = chassisDao.create();

        pod.setName(discoveryConfig.get(DiscoveryConfig.class).getPodName());
        pod.setChassisType(POD);
        pod.setLocationId(podLocation);
        pod.setTheId(id("pod"));
        pod.setStatus(new Status(ENABLED, OK, null));

        return pod;
    }
    
}

