/*
 * Copyright (c) 2015-2018 Intel Corporation
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

import static com.intel.podm.business.entities.redfish.base.StatusControl.statusOf;
import static com.intel.podm.business.redfish.services.impl.ComputerSystemUpdater.validateBootSupport;
import static java.util.Optional.ofNullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.ResourceStateMismatchException;
import com.inspur.podm.api.business.services.context.Context;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.redfish.services.EntityTreeTraverser;
import com.intel.podm.business.redfish.services.actions.ComputerSystemUpdateInvoker;
import com.intel.podm.common.types.BootSourceType;
import com.intel.podm.common.types.actions.ComputerSystemUpdateDefinition;
import com.intel.podm.common.types.redfish.RedfishComputerSystem;

@Component
public class ComposedNodeUpdater {
    @Autowired
    private EntityTreeTraverser traverser;

    @Autowired
    private ComputerSystemUpdateInvoker computerSystemUpdateInvoker;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateComposedNode(Context context, RedfishComputerSystem representation) throws BusinessApiException {
        ComposedNode node = (ComposedNode) traverser.traverse(context);
        ComputerSystem computerSystem = node.getComputerSystem();
        validate(computerSystem);

        Boolean clearTpmOnDelete = representation.getClearTpmOnDelete();
        ofNullable(clearTpmOnDelete).ifPresent(node::setClearTpmOnDelete);

        if (representation.getBoot() != null) {
            ComputerSystemUpdateDefinition updateDefinition = createComputerSystemUpdateDefinition(representation);
            validateBootSupportForComposedNode(updateDefinition.getBootSourceType(), computerSystem);

            computerSystemUpdateInvoker.updateComputerSystem(computerSystem, updateDefinition);
        }
    }

    private ComputerSystemUpdateDefinition createComputerSystemUpdateDefinition(RedfishComputerSystem representation) {
        ComputerSystemUpdateDefinition computerSystemUpdateDefinition = new ComputerSystemUpdateDefinition();

        RedfishComputerSystem.Boot boot = representation.getBoot();
        if (boot != null) {
            computerSystemUpdateDefinition.setBootSourceMode(boot.getBootSourceOverrideMode());
            computerSystemUpdateDefinition.setBootSourceState(boot.getBootSourceOverrideEnabled());
            computerSystemUpdateDefinition.setBootSourceType(boot.getBootSourceOverrideTarget());
        }

        return computerSystemUpdateDefinition;
    }

    private void validate(ComputerSystem computerSystem) throws ResourceStateMismatchException {
        if (!statusOf(computerSystem).isEnabled().isHealthy().verify()) {
            throw new ResourceStateMismatchException("Computer System should be enabled and healthy in order to invoke actions on it.");
        }
    }

    private void validateBootSupportForComposedNode(BootSourceType type, ComputerSystem computerSystem) throws BusinessApiException {
        validateBootSupport(type, computerSystem);
    }
}
