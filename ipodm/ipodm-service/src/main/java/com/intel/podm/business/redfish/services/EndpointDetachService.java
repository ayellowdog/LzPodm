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

package com.intel.podm.business.redfish.services;


import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.EntityOperationException;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.Endpoint;
import com.intel.podm.business.entities.redfish.Volume;
import com.intel.podm.business.redfish.services.actions.VolumeInitializeActionInvoker;
import com.intel.podm.business.redfish.services.actions.ZoneActionsInvoker;

@Component
public class EndpointDetachService {
    @Autowired
    private ZoneActionsInvoker zoneActionsInvoker;

    @Autowired
    private VolumeInitializeActionInvoker volumeInitializeActionInvoker;

    @Transactional(propagation = Propagation.MANDATORY)
    public void detachEndpoint(ComposedNode composedNode, Endpoint endpoint) throws BusinessApiException {
        unlinkEndpointFromComposedNode(composedNode, endpoint);
        unlinkEndpointFromZone(endpoint);
        deallocateEndpoint(endpoint);

        endpoint.getVolumes().stream()
            .filter(Volume::isErasePossible)
            .forEach(volume -> {
                try {
                    volumeInitializeActionInvoker.initialize(volume, null);
                } catch (EntityOperationException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            });
    }

    private void unlinkEndpointFromComposedNode(ComposedNode composedNode, Endpoint endpoint) {
        composedNode.unlinkEndpoint(endpoint);
    }

    private void deallocateEndpoint(Endpoint endpoint) {
        endpoint.getMetadata().setAllocated(false);
    }

    public void unlinkEndpointFromZone(Endpoint endpoint) throws BusinessApiException {
        zoneActionsInvoker.updateZone(endpoint.getZone(), getEndpointsSetForUpdate(endpoint));
    }

    private Set<Endpoint> getEndpointsSetForUpdate(Endpoint endpoint) {
        Set<Endpoint> zoneEndpointCollection = new HashSet<>(endpoint.getZone().getEndpoints());
        zoneEndpointCollection.remove(endpoint);

        return zoneEndpointCollection;
    }

}
