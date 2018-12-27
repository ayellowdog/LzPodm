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

package com.intel.podm.business.redfish.services.attach.strategy;

import static com.inspur.podm.api.business.Violations.createWithViolations;
import static com.inspur.podm.api.business.services.context.ContextType.ENDPOINT;
import static com.intel.podm.common.types.EntityRole.TARGET;
import static com.intel.podm.common.types.Protocol.NVME_OVER_FABRICS;
import static java.lang.String.format;

import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.RequestValidationException;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.context.ContextType;
import com.inspur.podm.api.business.services.redfish.requests.AttachResourceRequest;
import com.intel.podm.business.entities.redfish.ConnectedEntity;
import com.intel.podm.business.entities.redfish.Endpoint;
import com.intel.podm.business.redfish.services.EntityTreeTraverser;
import com.intel.podm.business.redfish.services.ServiceTraverser;
import com.intel.podm.business.redfish.services.attach.AttachResourceStrategy;
import com.intel.podm.business.redfish.services.impl.EndpointActionService;
import com.intel.podm.common.synchronization.TaskCoordinator;
import com.intel.podm.common.types.Protocol;

@Component
public class EndpointAttachStrategy implements AttachResourceStrategy {
    @Autowired
    private EntityTreeTraverser entityTraverser;

    @Autowired
    private TaskCoordinator taskCoordinator;

    @Autowired
    private ServiceTraverser traverser;

    @Autowired
    private EndpointActionService endpointActionService;

    @Override
    public ContextType supportedType() {
        return ENDPOINT;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void attach(Context target, Context resourceContext) throws BusinessApiException, TimeoutException {
        taskCoordinator.run(traverser.traverseServiceUuid(resourceContext), () -> endpointActionService.attachEndpoint(target, resourceContext));
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void validate(Context target, AttachResourceRequest request) throws BusinessApiException {
        Endpoint endpoint = (Endpoint) entityTraverser.traverse(request.getResourceContext());
        validateEndpointConnectedEntities(endpoint);
        validateEndpointAttachAbility(endpoint);
        validateEndpointProtocol(request.getProtocol(), endpoint);
    }

    private void validateEndpointConnectedEntities(Endpoint endpoint) throws RequestValidationException {
        Set<ConnectedEntity> connectedEntities = endpoint.getConnectedEntities();
        if (connectedEntities == null || connectedEntities.isEmpty()) {
            throw new RequestValidationException(createWithViolations("Selected Endpoint should have at least one ConnectedEntity"));
        }
        if (!endpoint.hasRole(TARGET)) {
            throw new RequestValidationException(createWithViolations("Only Target Endpoints are allowed to attach."));
        }
    }

    private void validateEndpointAttachAbility(Endpoint endpoint) throws RequestValidationException {
        if (!endpoint.isAttachable()) {
            throw new RequestValidationException(createWithViolations("Selected Endpoint is currently in use!"));
        }
    }

    private void validateEndpointProtocol(Protocol protocol, Endpoint endpoint) throws RequestValidationException {
        Protocol fabricType = endpoint.getFabric().getFabricType();
        if (!NVME_OVER_FABRICS.equals(fabricType)) {
            throw new RequestValidationException(createWithViolations("Only Endpoints from NVMeOF protocol are allowed to attach"));
        }
        if (protocol != null && !protocol.equals(fabricType)) {
            throw new RequestValidationException(createWithViolations(format("Endpoint protocol does not match chosen protocol: %s", protocol)));
        }
    }
}
