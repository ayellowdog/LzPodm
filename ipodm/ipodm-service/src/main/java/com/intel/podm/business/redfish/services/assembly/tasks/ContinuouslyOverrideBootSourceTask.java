/*
 * Copyright (c) 2016-2018 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.podm.business.redfish.services.assembly.tasks;

import static com.intel.podm.common.types.BootSourceState.CONTINUOUS;
import static com.intel.podm.common.types.BootSourceType.HDD;
import static com.intel.podm.common.types.BootSourceType.PXE;
import static com.intel.podm.common.types.BootSourceType.REMOTE_DRIVE;
import static com.intel.podm.common.types.Protocol.ISCSI;

import java.util.Collection;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.EntityOperationException;
import com.intel.podm.business.entities.dao.GenericDao;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.NetworkInterface;
import com.intel.podm.business.redfish.services.actions.ComputerSystemUpdateInvoker;
//import com.intel.podm.common.enterprise.utils.logger.TimeMeasured;
import com.intel.podm.common.types.BootSourceType;
import com.intel.podm.common.types.actions.ComputerSystemUpdateDefinition;

@Component
public class ContinuouslyOverrideBootSourceTask extends NodeTask {
    @Autowired
    private ComputerSystemUpdateInvoker computerSystemUpdateInvoker;

    @Autowired
    private GenericDao genericDao;

    @Override
//    @TimeMeasured(tag = "[AssemblyTask]")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void run() {
        ComposedNode composedNode = genericDao.find(ComposedNode.class, nodeId);
        ComputerSystem computerSystem = getComputerSystemFromNode(composedNode);
        BootSourceType bootSourceType = getProperBootSource(composedNode, computerSystem);
        ComputerSystemUpdateDefinition computerSystemUpdateDefinition =
            new ComputerSystemUpdateDefinition(null, bootSourceType, CONTINUOUS);
        try {
            computerSystemUpdateInvoker.updateComputerSystem(computerSystem, computerSystemUpdateDefinition);
        } catch (EntityOperationException e) {
            throw new RuntimeException("Assembly failed on Computer system action", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID getServiceUuid() {
        return getAssociatedComputeServiceUuid(genericDao.find(ComposedNode.class, nodeId));
    }

    private BootSourceType getProperBootSource(ComposedNode composedNode, ComputerSystem computerSystem) {
        return composedNode.getEndpoints().stream().noneMatch(endpoint -> ISCSI.equals(endpoint.getProtocol()))
            ? HDD
            : getProperBootSourceForComputerSystem(computerSystem);
    }

    private BootSourceType getProperBootSourceForComputerSystem(ComputerSystem computerSystem) {
        return computerSystem.getNetworkInterfaces().stream()
            .map(NetworkInterface::getNetworkDeviceFunctions)
            .mapToInt(Collection::size)
            .sum() > 0 ? REMOTE_DRIVE : PXE;
    }
}
