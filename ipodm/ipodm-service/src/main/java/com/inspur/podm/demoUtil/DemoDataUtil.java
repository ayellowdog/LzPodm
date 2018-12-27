/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.inspur.podm.demoUtil;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.intel.podm.common.types.ServiceType;
import com.intel.podm.services.detection.dhcp.DhcpServiceCandidate;

/**
 * @ClassName: DemoDataUtil
 * @Description: TODO
 *
 * @author: liuchangbj
 * @date: 2018年12月25日 下午3:27:43
 */
public class DemoDataUtil {
	private static LocalDateTime initTime = LocalDateTime.now();
	public static Set<DhcpServiceCandidate> getEndpointCandidates() {
		ServiceType serviceType = ServiceType.PSME;
		URI endpointUri = URI.create("http://144.34.216.66:9443/redfish/v1");
		//每次时间都一样，因此不会重复触发detect
		LocalDateTime updateDate = initTime;
		DhcpServiceCandidate tmp = new DhcpServiceCandidate(serviceType, endpointUri, updateDate);
		Set<DhcpServiceCandidate> candidateSet = new HashSet<>();
		candidateSet.add(tmp);
		return candidateSet;
	}
}

