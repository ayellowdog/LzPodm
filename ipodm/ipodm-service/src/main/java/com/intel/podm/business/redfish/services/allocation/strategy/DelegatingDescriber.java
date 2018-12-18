/**
 *<p> Copyright © 2018 Inspur Group Co.,Ltd.  版权所有 浪潮集团有限公司 </p>.
 */
package com.intel.podm.business.redfish.services.allocation.strategy;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.services.redfish.requests.RequestedNode;

/**
 * @ClassName: DelegatingDescriber
 * @Description: TODO
 *
 * @author: zhangdian
 * @date: 2018年12月17日 下午5:21:06
 */
@Component("delegatingDescriber")
public class DelegatingDescriber implements RemoteDriveAllocationContextDescriber {
    @Resource(name = "zdDriveAllocationContextDescriber")
    private RemoteDriveAllocationContextDescriber newDriveAllocationContextDescriber;

    @Resource(name = "masterBasedNewDriveAllocationContextDescriber")
    private RemoteDriveAllocationContextDescriber masterBasedNewDriveAllocationContextDescriber;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public RemoteDriveAllocationContextDescriptor describe(RequestedNode.RemoteDrive remoteDrive) {
        if (remoteDrive.getMaster() != null) {
            return masterBasedNewDriveAllocationContextDescriber.describe(remoteDrive);
        }

        return newDriveAllocationContextDescriber.describe(remoteDrive);
    }
}
