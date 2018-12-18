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

import static com.intel.podm.business.redfish.services.Contexts.toContext;
import static com.intel.podm.common.types.EntityRole.INITIATOR;
import static com.intel.podm.common.types.EntityRole.TARGET;
import static com.intel.podm.common.types.Protocol.ISCSI;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.Violations;
import com.inspur.podm.api.business.services.redfish.requests.RequestedNode;
import com.intel.podm.business.entities.dao.GenericDao;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.StoragePool;
import com.intel.podm.business.redfish.services.allocation.strategy.matcher.NewDriveAllocationContextDescriber;
import com.intel.podm.business.redfish.services.allocation.validation.NewRemoteDriveValidator;
import com.intel.podm.business.redfish.services.assembly.IscsiAssemblyTasksProvider;
import com.intel.podm.business.redfish.services.assembly.tasks.EndpointTaskFactory;
import com.intel.podm.business.redfish.services.assembly.tasks.NewVolumeTaskFactory;
import com.intel.podm.business.redfish.services.assembly.tasks.NodeTask;
import com.intel.podm.business.redfish.services.assembly.tasks.ZoneTaskFactory;
import com.intel.podm.discovery.external.partial.EndpointObtainer;

@Component
@Transactional(propagation = Propagation.MANDATORY)
@SuppressWarnings({"checkstyle:ClassFanOutComplexity"})
public class NewRemoteDriveAllocationStrategy implements RemoteDriveAllocationStrategy {
    @Autowired
    private NewRemoteDriveValidator validator;

    @Autowired
    private NewVolumeTaskFactory newVolumeTaskFactory;

    @Autowired
    private EndpointTaskFactory endpointTaskFactory;

    @Autowired
    private ZoneTaskFactory zoneTaskFactory;

    @Autowired
    private EndpointObtainer endpointObtainer;

    @Autowired
    private IscsiAssemblyTasksProvider iscsiAssemblyTasksProvider;

    @Autowired
    private GenericDao genericDao;

    @Resource(name="delegatingDescriber")
    private RemoteDriveAllocationContextDescriber driveDescriptorFinder;

    private RequestedNode.RemoteDrive drive;
    private RemoteDriveAllocationContextDescriptor descriptor;
    private List<NodeTask> tasks = new ArrayList<>();

    public void setDrive(RequestedNode.RemoteDrive drive) {
        this.drive = drive;
    }

    @Override
    public Violations validate() {
        return validator.validate(drive);
    }

    @Override
    public Violations findResources() {
        descriptor = driveDescriptorFinder.describe(drive);

        return descriptor.getViolations();
    }

    @Override
    public void allocate(ComposedNode composedNode) {
        StoragePool storagePool = genericDao.find(StoragePool.class, descriptor.getStoragePoolId());
        reserveSpaceOnLogicalVolumeGroup(composedNode, storagePool);
        tasks.addAll(newVolumeTaskFactory.createTasks(descriptor));

        tasks.add(endpointTaskFactory.create(TARGET));

        ComputerSystem computerSystem = composedNode.getComputerSystem();
        if (endpointObtainer.getInitiatorEndpoint(computerSystem, storagePool.getService()) == null) {
            tasks.add(endpointTaskFactory.create(INITIATOR, toContext(computerSystem)));
        }

        if (computerSystem.hasNetworkInterfaceWithNetworkDeviceFunction() && ISCSI.equals(descriptor.getProtocol())) {
            tasks.addAll(iscsiAssemblyTasksProvider.createTasks());
        }
        tasks.add(zoneTaskFactory.create());
    }

    @Override
    public List<NodeTask> getTasks() {
        return tasks;
    }

    private void reserveSpaceOnLogicalVolumeGroup(ComposedNode composedNode, StoragePool storagePool) {
        composedNode.setRemoteDriveCapacityGib(descriptor.getCapacity());
        storagePool.addComposedNode(composedNode);
    }

}
