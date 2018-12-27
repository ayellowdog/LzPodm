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

package com.intel.podm.business.redfish.services.impl;

import static com.inspur.podm.api.business.Violations.createWithViolations;
import static com.inspur.podm.api.business.Violations.ofValueNotAllowedViolation;
import static com.intel.podm.common.types.ComposedNodeState.ASSEMBLED;
import static com.intel.podm.common.types.ComposedNodeState.FAILED;
import static java.lang.String.format;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.List;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.RequestValidationException;
import com.inspur.podm.api.business.ResourceStateMismatchException;
import com.inspur.podm.api.business.Violations;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.ActionService;
import com.inspur.podm.api.business.services.redfish.requests.ResetRequest;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.base.Entity;
import com.intel.podm.business.entities.redfish.base.Resettable;
import com.intel.podm.business.redfish.services.EntityTreeTraverser;
import com.intel.podm.business.redfish.services.actions.ResetActionInvoker;
import com.intel.podm.business.redfish.services.allocation.strategy.EthernetInterfacesAllocator;
import com.intel.podm.common.synchronization.TaskCoordinator;
import com.intel.podm.common.types.actions.ResetType;


@Service("resetActionServiceImpl")
class ResetActionServiceImpl implements ActionService<ResetRequest> {
    @Autowired
    private ResetActionInvoker invoker;

    @Autowired
    private EntityTreeTraverser traverser;

    @Autowired
    private TaskCoordinator taskCoordinator;

    private static final Logger logger = LoggerFactory.getLogger(EthernetInterfacesAllocator.class);

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void perform(Context target, ResetRequest request) throws BusinessApiException, TimeoutException {
        ResetType resetType = validateResetRequest(request);

        Entity entity = traverser.traverse(target);
        final Resettable resettableEntity = getResettableEntity(entity);
        validateResettableStatus(resettableEntity);
        validateResetType(resettableEntity, resetType);

        taskCoordinator.run(resettableEntity.getService().getUuid(), () -> invoker.reset(resettableEntity, resetType));
    }

    private Resettable getResettableEntity(Entity entity) throws BusinessApiException {
        Resettable resettableEntity = null;
        if (entity instanceof Resettable) {
            resettableEntity = (Resettable) entity;
        } else if (entity instanceof ComposedNode) {
            ComposedNode composedNode = (ComposedNode) entity;
            validateComposedNodeState(composedNode);
            resettableEntity = getComputerSystem(composedNode);
        }
        if (resettableEntity == null) {
            throw new BusinessApiException("ResetAction not applicable to entity " + entity.getClass().getSimpleName());
        }
        return resettableEntity;
    }

    private ResetType validateResetRequest(ResetRequest request) throws RequestValidationException {
        ResetType resetType = request.getResetType();
        if (resetType == null) {
            Violations violations = new Violations();
            violations.addMissingPropertyViolation("ResetType");
            throw new RequestValidationException(violations);
        }
        return resetType;
    }

    private ComputerSystem getComputerSystem(ComposedNode composedNode) throws ResourceStateMismatchException {
        ComputerSystem computerSystem = composedNode.getComputerSystem();
        if (computerSystem == null) {
            throw new ResourceStateMismatchException("No ComputerSystem is associated with ComposedNode. Action aborted!");
        }
        return computerSystem;
    }

    private void validateComposedNodeState(ComposedNode composedNode) throws ResourceStateMismatchException {
        if (!composedNode.isInAnyOfStates(ASSEMBLED, FAILED)) {
            throw new ResourceStateMismatchException("ComposedNode state is invalid for requested power action: " + composedNode.getComposedNodeState());
        }
    }

    private void validateResetType(Resettable resettable, ResetType resetType) throws BusinessApiException {
        List<ResetType> supportedResetTypes = resettable.getAllowableResetTypes();

        if (isEmpty(supportedResetTypes)) {
            String violation = format("Reset action not allowed on resource %s. There are no allowable reset types.", resettable.getSourceUri());
            throw new RequestValidationException(createWithViolations(violation));
        }

        if (!supportedResetTypes.contains(resetType)) {
            throw new RequestValidationException(ofValueNotAllowedViolation("ResetType", supportedResetTypes));
        }
    }

    private void validateResettableStatus(Resettable entity) throws ResourceStateMismatchException {
        if (!entity.isPresent()) {
            throw new ResourceStateMismatchException("ComputerSystem should be enabled and healthy in order to invoke actions on it.");
        }
    }
}
