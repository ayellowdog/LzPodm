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

package com.intel.podm.business.redfish.services.actions;

import static com.intel.podm.common.utils.Contracts.requiresNonNull;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.EntityOperationException;
import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.business.entities.redfish.Fabric;
import com.intel.podm.business.entities.redfish.Zone;
import com.intel.podm.client.WebClient;
import com.intel.podm.client.WebClientBuilder;
import com.intel.podm.client.WebClientRequestException;
import com.intel.podm.client.actions.ZoneActionJson;
import com.intel.podm.common.types.actions.ZoneActionRequest;
import com.intel.podm.discovery.external.partial.ZoneObtainer;

@Component
public class FabricActionsInvoker {
    @Autowired
    private WebClientBuilder webClientBuilder;

    private static final Logger logger = LoggerFactory.getLogger(FabricActionsInvoker.class);

    @Autowired
    private ZoneObtainer zoneObtainer;

    @Transactional(propagation = Propagation.MANDATORY)
    public Zone createZone(Fabric associatedFabric, ZoneActionRequest zoneActionRequest) throws EntityOperationException {
        ExternalService service = associatedFabric.getService();
        requiresNonNull(service, "service", "There is no Service associated with selected Fabric");

        URI fabricSourceUri = associatedFabric.getSourceUri();
        URI zoneUri = performCreateZoneAction(
            new ZoneActionJson(zoneActionRequest.getLinks().getEndpoints()),
            service,
            fabricSourceUri);
        try {
            return zoneObtainer.discoverZone(service, associatedFabric, zoneUri);
        } catch (WebClientRequestException e) {
            String errorMessage = "Zone refreshing failed on selected Fabric";
            logger.warn(errorMessage + " on [ service: {}, path: {} ]", service.getBaseUri(), zoneUri);
            throw new EntityOperationException(errorMessage, e);
        }
    }

    private URI performCreateZoneAction(ZoneActionJson zoneActionJson, ExternalService service, URI fabricUri) throws EntityOperationException {
        URI ruleUri;
        try (WebClient webClient = webClientBuilder.newInstance(service.getBaseUri()).retryable().build()) {
            URI zonesCollectionUri = URI.create(fabricUri + "/Zones");
            ruleUri = webClient.post(zonesCollectionUri, zoneActionJson);
        } catch (WebClientRequestException e) {
            String errorMessage = "Zone creation failed on selected Fabric";
            logger.warn(errorMessage + " on [ service: {}, path: {} ]", service.getBaseUri(), fabricUri);
            throw new EntityOperationException(errorMessage, e);
        }
        return ruleUri;
    }
}
