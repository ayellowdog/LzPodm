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

import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.RequestValidationException;
import com.inspur.podm.api.business.Violations;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.CreationService;
import com.intel.podm.business.redfish.services.ServiceTraverser;
import com.intel.podm.business.redfish.services.VlanNetworkInterfaceActionsService;
import com.intel.podm.common.synchronization.TaskCoordinator;
import com.intel.podm.common.types.redfish.RedfishVlanNetworkInterface;

@Service("VlanNetworkInterfaceCreationService")
class VlanNetworkInterfaceCreationServiceImpl implements CreationService<RedfishVlanNetworkInterface> {
    @Autowired
    private TaskCoordinator taskCoordinator;

    @Autowired
    private ServiceTraverser traverser;

    @Autowired
    private VlanNetworkInterfaceActionsService vlanNetworkInterfaceActionsService;

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public Context create(Context creationalContext, RedfishVlanNetworkInterface representation) throws BusinessApiException, TimeoutException {
        validate(representation);

        return taskCoordinator.call(
            traverser.traverseServiceUuid(creationalContext),
            () -> vlanNetworkInterfaceActionsService.createVlan(creationalContext, representation)
        );
    }

    private void validate(RedfishVlanNetworkInterface representation) throws RequestValidationException {
        Violations violations = new Violations();
        if (representation.getVlanId() == null) {
            violations.addMissingPropertyViolation("VLANId");
        } else if (representation.getTagged() == null) {
            violations.addMissingPropertyViolation("Tagged");
        } else if (representation.getVlanEnable() == null) {
            violations.addMissingPropertyViolation("VLANEnable");
        }

        if (violations.hasViolations()) {
            throw new RequestValidationException(violations);
        }
    }
}
