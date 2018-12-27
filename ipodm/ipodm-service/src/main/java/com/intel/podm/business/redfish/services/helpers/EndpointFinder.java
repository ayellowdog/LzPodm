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

package com.intel.podm.business.redfish.services.helpers;

import static com.intel.podm.business.redfish.services.Contexts.toContext;
import static com.intel.podm.business.redfish.services.helpers.EndpointCreationRequestBuilder.buildInitiatorEndpointCreationRequest;
import static com.intel.podm.business.redfish.services.helpers.EndpointCreationRequestBuilder.buildTargetEndpointCreationRequest;
import static java.util.stream.Collectors.toSet;

import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.services.context.Context;
import com.intel.podm.business.entities.dao.GenericDao;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.Endpoint;
import com.intel.podm.business.entities.redfish.Fabric;
import com.intel.podm.business.entities.redfish.Volume;
import com.intel.podm.business.redfish.services.EndpointCreationService;
import com.intel.podm.discovery.external.partial.EndpointObtainer;

@Component
public class EndpointFinder {
    @Autowired
    private EndpointObtainer endpointObtainer;

    @Autowired
    private EndpointCreationService endpointCreationService;

    @Autowired
    private GenericDao genericDao;

    @Autowired
    private VolumeHelper volumeHelper;

    @Transactional(propagation = Propagation.MANDATORY)
    public Endpoint getOrCreateInitiatorEndpoint(ComputerSystem computerSystem, Fabric fabric) throws BusinessApiException {
        Endpoint initiator = endpointObtainer.getInitiatorEndpoint(computerSystem, fabric);

        if (initiator == null) {
            UUID computerSystemUuid = computerSystem.getUuid();
            Context endpointContext = endpointCreationService
                .createAndDiscoverEndpoint(fabric, buildInitiatorEndpointCreationRequest(fabric, computerSystemUuid));
            Endpoint createdEndpoint = genericDao.find(Endpoint.class, endpointContext.getId());
            computerSystem.addEndpoint(createdEndpoint);

            return createdEndpoint;
        }

        return initiator;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Endpoint getOrCreateTargetEndpoint(Volume volume) throws BusinessApiException {
        if (volume.getEndpoints().isEmpty()) {
            Fabric fabric = volumeHelper.retrieveFabricFromVolume(volume);
            Context endpointContext = endpointCreationService
                .createAndDiscoverEndpoint(fabric, buildTargetEndpointCreationRequest(fabric, toContext(volume)));

            return genericDao.find(Endpoint.class, endpointContext.getId());
        }
        Set<Endpoint> attachableEndpoints = getAttachableEndpoints(volume);

        return attachableEndpoints.stream().findFirst().get();
    }

    public Set<Endpoint> getAttachableEndpoints(Volume volume) {
        return volume.getEndpoints().stream()
            .filter(Endpoint::isAttachable)
            .collect(toSet());
    }
}
