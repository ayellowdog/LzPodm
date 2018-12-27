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

package com.intel.podm.business.redfish.services.impl;


import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.Endpoint;
import com.intel.podm.business.redfish.services.actions.ZoneActionsInvoker;
import com.intel.podm.business.redfish.services.helpers.EndpointFinder;

@Component
public class EndpointAttacher {
    @Autowired
    private EndpointFinder endpointFinder;

    @Autowired
    private ZoneActionsInvoker zoneActionsInvoker;

    @Transactional(propagation = Propagation.MANDATORY)
    public void attachEndpoint(ComposedNode composedNode, Endpoint endpoint) throws BusinessApiException {
        Endpoint initiator = endpointFinder.getOrCreateInitiatorEndpoint(composedNode.getComputerSystem(), endpoint.getFabric());
        attach(composedNode, endpoint);

        Set<Endpoint> zoneEndpoints = composedNode.getEndpoints();
        zoneEndpoints.add(initiator);
        zoneActionsInvoker.createOrUpdateZone(initiator.getZone(), zoneEndpoints, initiator.getFabric());
    }

    private void attach(ComposedNode composedNode, Endpoint endpoint) {
        composedNode.addEndpoint(endpoint);
        endpoint.getMetadata().setAllocated(true);
        endpoint.getVolumes().forEach(volume -> {
            volume.getMetadata().setAllocated(true);
            composedNode.addVolume(volume);
        });
    }
}
