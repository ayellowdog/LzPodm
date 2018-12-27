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

import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.RequestValidationException;
import com.inspur.podm.api.business.dto.actions.actionInfo.ActionInfoDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.ActionService;
import com.inspur.podm.api.business.services.redfish.requests.DetachResourceRequest;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.redfish.services.EntityTreeTraverser;
import com.intel.podm.business.redfish.services.actions.DetachResourceInfoService;
import com.intel.podm.business.redfish.services.detach.DetachResourceStrategy;
import com.intel.podm.business.redfish.services.detach.DetachResourceStrategyFactory;
import com.intel.podm.business.redfish.services.helpers.NodeActionsValidator;

@Service("detachResourceActionServiceImpl")
class DetachResourceActionServiceImpl implements ActionService<DetachResourceRequest> {
    @Autowired
    private DetachResourceStrategyFactory detachResourceStrategyFactory;

    @Autowired
    private NodeActionsValidator nodeActionHelper;

    @Autowired
    private DetachResourceInfoService detachResourceInfoService;

    @Autowired
    private EntityTreeTraverser traverser;

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void perform(Context composedNodeContext, DetachResourceRequest request) throws BusinessApiException, TimeoutException {
        validateRequest(request);
        nodeActionHelper.validateIfActionCanBePerformedOnNode(composedNodeContext);

        Context resourceToDetach = request.getResourceContext();
        DetachResourceStrategy detachResourceStrategy = detachResourceStrategyFactory.getStrategyForResource(resourceToDetach.getType());
        detachResourceStrategy.detach(composedNodeContext, resourceToDetach);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ActionInfoDto getActionInfo(Context target) throws BusinessApiException {
        ComposedNode composedNode = (ComposedNode) traverser.traverse(target);
        return detachResourceInfoService.getActionInfo(composedNode);
    }

    private void validateRequest(DetachResourceRequest request) throws RequestValidationException {
        if (request.getResourceContext() == null) {
            throw new RequestValidationException(createWithViolations("Resource needs to be specified"));
        }
    }
}
