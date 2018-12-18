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

package com.intel.podm.business.redfish.services.allocation.strategy;

import static com.intel.podm.common.types.ComposedNodeState.ALLOCATING;
import static com.intel.podm.common.types.Health.OK;
import static com.intel.podm.common.types.State.ENABLED;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.Violations;
import com.inspur.podm.api.business.services.redfish.requests.RequestedNode;
import com.intel.podm.business.entities.dao.ComputerSystemDao;
import com.intel.podm.business.entities.dao.GenericDao;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.base.LocalStorage;
import com.intel.podm.business.redfish.services.allocation.strategy.matcher.ComputerSystemMatcher;
import com.intel.podm.business.redfish.services.allocation.strategy.matcher.LocalStorageCollector;
import com.intel.podm.business.redfish.services.allocation.strategy.matcher.PcieLocalStorage;
import com.intel.podm.business.redfish.services.allocation.validation.ComputerSystemAllocationValidator;
import com.intel.podm.business.redfish.services.assembly.tasks.NodeTask;
import com.intel.podm.common.types.Status;

@Component
@Transactional(propagation = Propagation.MANDATORY)
public class ComputerSystemAllocationStrategy {
    @Autowired
    private ComputerSystemDao computerSystemDao;

    @Autowired
    private GenericDao genericDao;

    @Autowired
    private ComputerSystemAllocationValidator validator;

    @Autowired
    private ComputerSystemMatcher computerSystemMatcher;

    @Autowired
    private EthernetInterfacesAllocator ethernetInterfacesAllocator;

    @Autowired
    private ComputerSystemConfigurationTaskFactory computerSystemConfigurationTaskFactory;

    @Autowired
    private PcieLocalStorageAllocator pcieLocalStorageAllocator;

    @Autowired
    private LocalStorageCollector localStorageCollector;

    private RequestedNode requestedNode;
    private List<NodeTask> tasks = new ArrayList<>();

    public void setRequestedNode(RequestedNode requestedNode) {
        this.requestedNode = requestedNode;
    }

    public Violations validate() {
        return validator.validate(requestedNode);
    }

    public ComputerSystem findResources() throws ResourceFinderException {
        Collection<ComputerSystem> computerSystems = computerSystemDao.getComputerSystemsPossibleToAllocate();
        computerSystems = computerSystemMatcher.matches(requestedNode, computerSystems);
        return computerSystems.iterator().next();
    }

    public ComposedNode allocateWithComputerSystem(ComputerSystem computerSystem) {
        ComposedNode composedNode = createComposedNodeWithComputerSystem(computerSystem);

        Set<LocalStorage> storageUnderComputerSystem = localStorageCollector.getStorageUnderComputerSystem(computerSystem);

        Set<PcieLocalStorage> selectedPcieLocalStorages = pcieLocalStorageAllocator.selectResources(
            requestedNode.getLocalDrives(),
            storageUnderComputerSystem
        );

        tasks.addAll(pcieLocalStorageAllocator.prepareNodeAssemblyTasks(selectedPcieLocalStorages, storageUnderComputerSystem));
        for (PcieLocalStorage localStorage : selectedPcieLocalStorages) {
            composedNode.addDrive(localStorage.getDrive());
            localStorage.getDrive().getMetadata().setAllocated(true);
            composedNode.incrementNumberOfRequestedDrives();
        }

        tasks.addAll(ethernetInterfacesAllocator.allocate(requestedNode.getEthernetInterfaces(), computerSystem.getEthernetInterfaces()));
        tasks.addAll(computerSystemConfigurationTaskFactory.createComputerSystemConfigurationTask(requestedNode.getSecurity(), computerSystem));

        composedNode.setClearTpmOnDelete(retrieveClearTpmOnDelete());

        return composedNode;
    }

    private boolean retrieveClearTpmOnDelete() {
        RequestedNode.Security security = requestedNode.getSecurity();
        if (security != null && security.getClearTpmOnDelete() != null) {
            return security.getClearTpmOnDelete();
        }

        return true;
    }

    private ComposedNode createComposedNodeWithComputerSystem(ComputerSystem computerSystem) {
        ComposedNode composedNode = createComposedNode();
        composedNode.setComputerSystem(computerSystem);
        composedNode.setAssociatedComputerSystemUuid(computerSystem.getUuid());
        composedNode.setAssociatedComputeServiceUuid(computerSystem.getService().getUuid());
        computerSystem.getMetadata().setAllocated(true);

        return composedNode;
    }

    private ComposedNode createComposedNode() {
        ComposedNode composedNode = genericDao.create(ComposedNode.class);
        composedNode.setName(requestedNode.getName());
        composedNode.setDescription(requestedNode.getDescription());
        composedNode.setComposedNodeState(ALLOCATING);
        composedNode.setStatus(new Status(ENABLED, OK, OK));

        return composedNode;
    }

    public List<NodeTask> getTasks() {
        return tasks;
    }
}
