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

import static com.inspur.podm.api.business.Violations.createWithViolations;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.RequestValidationException;
import com.inspur.podm.api.business.services.context.Context;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.Drive;
import com.intel.podm.business.redfish.services.actions.PcieZoneActionsInvoker;
import com.intel.podm.business.redfish.services.allocation.strategy.matcher.LocalStorageCollector;
import com.intel.podm.business.redfish.services.allocation.strategy.matcher.PcieLocalStorage;

@Component
public class PcieZoneAttacher {
    @Autowired
    private EntityTreeTraverser traverser;

    @Autowired
    private LocalStorageCollector localStorageCollector;

    @Autowired
    private PcieZoneActionsInvoker pcieZoneActionsInvoker;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void attachEndpointToZone(Context composedNodeContext, Context pcieDriveContext) throws BusinessApiException {
        ComposedNode composedNode = (ComposedNode) traverser.traverse(composedNodeContext);

        Drive pcieDrive = (Drive) traverser.traverse(pcieDriveContext);
        Optional<PcieLocalStorage> pcieLocalStorage = findPcieLocalStorageByResource(composedNode.getComputerSystem(), pcieDrive);
        PcieLocalStorage localStorage = pcieLocalStorage
            .orElseThrow(() -> new RequestValidationException(createWithViolations("Provided PCIeDrive cannot be attached to this Node.")));

        pcieZoneActionsInvoker.attachEndpoint(localStorage.getZone(), localStorage.getEndpoint());
    }

    private Optional<PcieLocalStorage> findPcieLocalStorageByResource(ComputerSystem computerSystem, Drive drive) {
        return localStorageCollector.getPcieStorage(computerSystem).stream()
            .filter(pcieLocalStorage -> Objects.equals(pcieLocalStorage.getDrive(), drive))
            .findFirst();
    }
}
