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
import com.intel.podm.business.entities.redfish.Endpoint;
import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.business.entities.redfish.embeddables.EndpointAuthentication;
import com.intel.podm.client.WebClient;
import com.intel.podm.client.WebClientBuilder;
import com.intel.podm.client.WebClientRequestException;
import com.intel.podm.client.actions.EndpointUpdateRequest;
import com.intel.podm.client.resources.redfish.EndpointResource;
import com.intel.podm.common.types.actions.EndpointUpdateDefinition;

@Component
public class EndpointUpdateInvoker {
    @Autowired
    private WebClientBuilder webClientBuilder;

    private static final Logger logger = LoggerFactory.getLogger(EndpointUpdateInvoker.class);

    @Transactional(propagation = Propagation.MANDATORY)
    public void updateEndpoint(Endpoint endpoint, EndpointUpdateDefinition endpointUpdateDefinition) throws EntityOperationException {
        requiresNonNull(endpoint, "endpoint");
        ExternalService service = endpoint.getService();
        requiresNonNull(service, "service ", "there is no Service associated with selected Endpoint");

        EndpointResource endpointResource = performUpdateEndpointAction(endpointUpdateDefinition, service, endpoint.getSourceUri());

        EndpointAuthentication authentication = new EndpointAuthentication();
        authentication.setUsername(endpointResource.getAuthentication().getUsername());
        authentication.setPassword(endpointResource.getAuthentication().getPassword());

        endpoint.setAuthentication(authentication);
    }

    private EndpointResource performUpdateEndpointAction(EndpointUpdateDefinition endpointUpdateDefinition, ExternalService service, URI sourceUri)
        throws EntityOperationException {
        try (WebClient webClient = webClientBuilder.newInstance(service.getBaseUri()).retryable().build()) {
            return webClient.patchAndRetrieve(sourceUri, new EndpointUpdateRequest(endpointUpdateDefinition));
        } catch (WebClientRequestException e) {
            String errorMessage = "Update on selected Endpoint failed";
            logger.warn("{} on [ service: {}, path: {} ]", errorMessage, service, sourceUri);
            throw new EntityOperationException(errorMessage, e);
        }
    }
}
