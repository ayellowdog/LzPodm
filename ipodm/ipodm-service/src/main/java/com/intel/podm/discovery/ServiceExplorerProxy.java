/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.intel.podm.discovery;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName: ServiceExplorerProxy
 * @Description: 这个类为了解决在DiscoveryStartUp这个类里，init方法的startMonitoringOfService
 * 没有事务的问题，不知道为啥源码里从上到下都是support而没有required.
 * 为了不改变源码的结构，做了封装
 *
 * @author: liuchangbj
 * @date: 2018年12月26日 下午3:18:55
 */
@Component
public class ServiceExplorerProxy {
	@Autowired
	private ServiceExplorer serviceExplorer;
	@Transactional
	public void startMonitoringOfService(UUID serviceUuid){
		serviceExplorer.startMonitoringOfService(serviceUuid);
	}
}

