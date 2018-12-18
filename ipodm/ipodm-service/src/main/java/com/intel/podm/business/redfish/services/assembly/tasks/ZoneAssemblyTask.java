/*
 * Copyright (c) 2017-2018 Intel Corporation
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

import static com.intel.podm.common.utils.IterableHelper.any;
import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;

import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.EntityOperationException;
import com.intel.podm.business.entities.dao.GenericDao;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.DiscoverableEntity;
import com.intel.podm.business.entities.redfish.Endpoint;
import com.intel.podm.business.redfish.services.actions.FabricActionsInvoker;
import com.intel.podm.business.redfish.services.actions.ZoneActionsInvoker;
//import com.intel.podm.common.enterprise.utils.logger.TimeMeasured;
import com.intel.podm.common.types.actions.ZoneActionRequest;
import com.intel.podm.discovery.external.partial.EndpointObtainer;

@Component
public class ZoneAssemblyTask extends NodeTask {
	
    private static final Logger logger = LoggerFactory.getLogger(ZoneAssemblyTask.class);

    @Autowired
    private GenericDao genericDao;

    @Autowired
    private EndpointObtainer endpointObtainer;

    @Autowired
    private FabricActionsInvoker fabricActionService;

    @Autowired
    private ZoneActionsInvoker invoker;

    private Endpoint initiatorEndpoint;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @TimeMeasured(tag = "[AssemblyTask]")
    public void run() {
        ComposedNode composedNode = getComposedNode();
        initiatorEndpoint = endpointObtainer.getInitiatorEndpoint(composedNode.getComputerSystem(), any(getComposedNode().getEndpoints()).getFabric());

        if (initiatorEndpoint == null) {
            throw new IllegalStateException(format("There is no initiator endpoint on ComputerSystem with UUID: %s",
                composedNode.getComputerSystem().getUuid()));
        }

        if (initiatorEndpoint.getZone() == null) {
            createZone(composedNode);
        } else {
            updateZone(composedNode);
        }
    }

    @Override
//    @TimeMeasured(tag = "[AssemblyTask]")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID getServiceUuid() {
        return any(getComposedNode().getEndpoints()).getService().getUuid();
    }

    private ComposedNode getComposedNode() {
        return genericDao.find(ComposedNode.class, nodeId);
    }

    private ZoneActionRequest buildCreationRequest(Set<Endpoint> targetEndpoints) {
        targetEndpoints.add(initiatorEndpoint);

        return new ZoneActionRequest(targetEndpoints.stream().map(DiscoverableEntity::getSourceUri).collect(toSet()));
    }

    private void updateZone(ComposedNode composedNode) {
        try {
            invoker.updateZone(initiatorEndpoint.getZone(), composedNode.getEndpoints());
        } catch (BusinessApiException e) {
            logger.error("Zone update failed for ComposedNode: {}, details: {}", nodeId, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void createZone(ComposedNode composedNode) {
        try {
            fabricActionService.createZone(initiatorEndpoint.getFabric(), buildCreationRequest(composedNode.getEndpoints()));
        } catch (EntityOperationException e) {
            logger.error("Zone creation failed for ComposedNode: {}, details: {}", nodeId, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
