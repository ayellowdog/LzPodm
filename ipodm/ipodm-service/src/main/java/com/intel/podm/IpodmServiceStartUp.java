/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.intel.podm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.intel.podm.business.redfish.services.detach.DetachResourceStrategyFactory;
import com.intel.podm.client.typeidresolver.ResourceProvider;
import com.intel.podm.common.logger.Logger;
import com.intel.podm.common.logger.LoggerFactory;
import com.intel.podm.discovery.DiscoveryStartup;
import com.intel.podm.discovery.internal.PodStartupDiscovery;
import com.intel.podm.services.detection.ServiceDetectionStartup;

/**
 * @ClassName: IpodmServiceStartUp
 * @Description: 在spring容器加载完成后，完成几个模块的初始化。
 *
 * @author: liuchangbj
 * @date: 2018年12月26日 下午4:18:40
 */
@Component
public class IpodmServiceStartUp implements CommandLineRunner{
	private static final Logger logger = LoggerFactory.getLogger(IpodmServiceStartUp.class);
@Autowired
private DiscoveryStartup discoveryStartup;
@Autowired
private ResourceProvider resourceProvider;
@Autowired
private PodStartupDiscovery podStartupDiscovery;
@Autowired
private ServiceDetectionStartup serviceDetectionStartup;
@Autowired
DetachResourceStrategyFactory detachResourceStrategyFactory;
	@Override
	public void run(String... args) throws Exception {
		logger.i("starting ipodm services");
		resourceProvider.resourceProvider();
		podStartupDiscovery.initInitialPod();
		discoveryStartup.initialize();
		serviceDetectionStartup.init();
		detachResourceStrategyFactory.init();
		logger.i("start ipodm services finish");
	}

}

