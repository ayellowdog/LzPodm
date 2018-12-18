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

package com.intel.podm.business.redfish.services.assembly.tasks;

import static java.lang.Boolean.FALSE;
import static java.util.Optional.ofNullable;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.EntityOperationException;
import com.intel.podm.business.entities.dao.GenericDao;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.redfish.services.actions.ComputerSystemUpdateInvoker;
import com.intel.podm.common.types.actions.ComputerSystemUpdateDefinition;

@Component
public class ComputerSystemDisassembleTask extends NodeRemovalTask {
    @Autowired
    private GenericDao genericDao;

    @Autowired
    private ComputerSystemUpdateInvoker computerSystemUpdateInvoker;

    private static final Logger logger = LoggerFactory.getLogger(ComputerSystemDisassembleTask.class);

    private ComposedNode composedNode;
    private ComputerSystem computerSystem;


    @Override
    public void deallocate() {
        setComposedNodeAndComputerSystem();
        if (computerSystem == null) {
            logger.error("Couldn't deallocate computerSystem for nodeId={}, computerSystem is null", nodeId);
            return;
        }
        computerSystem.getMetadata().setAllocated(false);
    }

    @Override
    public void disassemble() {
        setComposedNodeAndComputerSystem();
        if (composedNode == null || computerSystem == null) {
            logger.error("Couldn't disassemble computerSystem for nodeId={}, computerSystem or composedNode is null", nodeId);
            return;
        }
        enableFirmwareUpdateOnComputerSystem();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID getServiceUuid() {
        return getAssociatedComputeServiceUuid(genericDao.find(ComposedNode.class, nodeId));
    }

    private void setComposedNodeAndComputerSystem() {
        composedNode = ofNullable(composedNode).orElse(genericDao.find(ComposedNode.class, nodeId));
        if (composedNode != null) {
            computerSystem = ofNullable(computerSystem).orElse(composedNode.getComputerSystem());
        }
    }

    private void enableFirmwareUpdateOnComputerSystem() {
        if (computerSystem.getUserModeEnabled() != null) {
            ComputerSystemUpdateDefinition computerSystemUpdateDefinition = new ComputerSystemUpdateDefinition();
            computerSystemUpdateDefinition.setUserModeEnabled(FALSE);
            try {
                computerSystemUpdateInvoker.updateComputerSystem(computerSystem, computerSystemUpdateDefinition);
            } catch (EntityOperationException e) {
                throw new RuntimeException("Enabling firmware update failed on computer system", e);
            }
        }
    }
}
