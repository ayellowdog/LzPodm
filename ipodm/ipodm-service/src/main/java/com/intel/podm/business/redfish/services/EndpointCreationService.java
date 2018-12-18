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

import static com.intel.podm.business.redfish.services.Contexts.toContext;
import static com.intel.podm.common.types.redfish.ResourceNames.ENDPOINTS_RESOURCE_NAME;
import static javax.ws.rs.core.UriBuilder.fromUri;

import java.net.URI;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.ContextResolvingException;
import com.inspur.podm.api.business.EntityOperationException;
import com.inspur.podm.api.business.dto.EndpointDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.CreationService;
import com.intel.podm.business.entities.redfish.Fabric;
import com.intel.podm.business.redfish.services.actions.RestRequestInvoker;
import com.intel.podm.client.WebClientRequestException;
import com.intel.podm.client.actions.EndpointCreationRequest;
import com.intel.podm.common.synchronization.TaskCoordinator;
import com.intel.podm.common.transactions.RequiresNewTransactionWrapper;
import com.intel.podm.discovery.external.partial.EndpointObtainer;

@Component
public class EndpointCreationService implements CreationService<EndpointDto> {
    @Autowired
    private TaskCoordinator taskCoordinator;

    @Autowired
    private ServiceTraverser traverser;

    @Autowired
    private EntityTreeTraverser entityTreeTraverser;

    @Autowired
    private RequiresNewTransactionWrapper transactionWrapper;

    @Autowired
    private ContextAwareMapper<EndpointDto, EndpointCreationRequest> contextAwareMapper;

    private static final Logger logger = LoggerFactory.getLogger(EndpointCreationService.class);

    @Autowired
    private RestRequestInvoker restRequestInvoker;

    @Autowired
    private EndpointObtainer endpointObtainer;

    @Override
    public Context create(Context fabricContext, EndpointDto representation) throws BusinessApiException, TimeoutException {
        return taskCoordinator.call(
            traverser.traverseServiceUuid(fabricContext),
            () -> createAndDiscoverEndpoint(fabricContext, representation)
        );
    }

    public Context createAndDiscoverEndpoint(Context fabricContext, EndpointDto representation) throws BusinessApiException {
        return transactionWrapper.run(() -> {
            Fabric fabric = (Fabric) entityTreeTraverser.traverse(fabricContext);
            return createAndDiscoverEndpoint(fabric, representation);
        });
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Context createAndDiscoverEndpoint(Fabric fabric, EndpointDto representation) throws BusinessApiException {
        EndpointCreationRequest endpointCreationRequest = createEndpointCreationRequest(representation);
        URI locationOfCreatedEndpoint = sendCreateEndpointRequest(endpointCreationRequest, fabric);

        return discoverCreatedEndpoint(fabric, locationOfCreatedEndpoint);
    }

    private Context discoverCreatedEndpoint(Fabric fabric, URI endpointUri) throws EntityOperationException {
        try {
            return toContext(endpointObtainer.discoverEndpoint(fabric.getService(), fabric, endpointUri));
        } catch (WebClientRequestException e) {
            String errorMessage = "Discovery of created Endpoint failed";
            logger.warn(errorMessage + " on [ service: {}, path: {} ]", fabric.getService(), endpointUri);
            throw new EntityOperationException(errorMessage, e);
        }
    }

    private EndpointCreationRequest createEndpointCreationRequest(EndpointDto representation) throws ContextResolvingException {
        return contextAwareMapper.map(representation, EndpointCreationRequest.class);
    }

    private URI sendCreateEndpointRequest(EndpointCreationRequest request, Fabric fabric) throws EntityOperationException {
        URI serviceUri = fabric.getService().getBaseUri();
        URI endpointsCollectionUri = fromUri(fabric.getSourceUri()).path(ENDPOINTS_RESOURCE_NAME).build();
        return restRequestInvoker.post(serviceUri, endpointsCollectionUri, request);
    }
}
