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
import static com.intel.podm.common.types.Protocol.ISCSI;
import static com.intel.podm.common.utils.IterableHelper.any;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.Violations;
import com.inspur.podm.api.business.services.redfish.requests.RequestedNode.RemoteDrive;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.Volume;
import com.intel.podm.business.redfish.services.allocation.validation.ExistingRemoteDriveValidator;
import com.intel.podm.business.redfish.services.assembly.IscsiAssemblyTasksProvider;
import com.intel.podm.business.redfish.services.assembly.tasks.EndpointTaskFactory;
import com.intel.podm.business.redfish.services.assembly.tasks.NodeTask;
import com.intel.podm.business.redfish.services.assembly.tasks.ZoneTaskFactory;
import com.intel.podm.business.redfish.services.helpers.VolumeHelper;
import com.intel.podm.discovery.external.partial.EndpointObtainer;

@Component
@Transactional(propagation = Propagation.REQUIRED)
public class ExistingRemoteDriveAllocationStrategy implements RemoteDriveAllocationStrategy {
    @Autowired
    private EndpointTaskFactory endpointTaskFactory;

    @Autowired
    private ZoneTaskFactory zoneTaskFactory;

    @Autowired
    private EndpointObtainer endpointObtainer;

    @Autowired
    private IscsiAssemblyTasksProvider iscsiAssemblyTasksProvider;

    @Autowired
    private VolumeHelper volumeHelper;

    @Autowired
    private ExistingRemoteDriveValidator existingRemoteDriveValidator;

    private RemoteDrive drive;
    private ResourceAllocationStrategy resourceAllocationStrategy;
    private List<NodeTask> tasks = new ArrayList<>();

    public void setResourceAllocationStrategy(ResourceAllocationStrategy resourceAllocationStrategy) {
        this.resourceAllocationStrategy = resourceAllocationStrategy;
    }

    public void setDrive(RemoteDrive drive) {
        this.drive = drive;
    }

    @Override
    public Violations validate() {
        Violations violations = new Violations();
        violations.addAll(resourceAllocationStrategy.validate());
        violations.addAll(existingRemoteDriveValidator.validateProtocolFromExistingRequestedDrive(drive));

        return violations;
    }

    @Override
    public Violations findResources() {
        Violations violations = new Violations();
        violations.addAll(resourceAllocationStrategy.findResources());
        return violations;
    }

    @Override
    public void allocate(ComposedNode composedNode) {
        tasks.addAll(resourceAllocationStrategy.allocate(composedNode));

        ComputerSystem computerSystem = composedNode.getComputerSystem();
        Volume volume = any(composedNode.getVolumes());
        if (endpointObtainer.getInitiatorEndpoint(computerSystem, volumeHelper.retrieveFabricFromVolume(volume)) == null) {
            tasks.add(endpointTaskFactory.create(volume.getTheId(), INITIATOR, toContext(computerSystem)));
        }
        if (computerSystem.hasNetworkInterfaceWithNetworkDeviceFunction() && ISCSI.equals(volumeHelper.retrieveProtocolFromVolume(volume))) {
            tasks.addAll(iscsiAssemblyTasksProvider.createTasks());
        }
        tasks.add(zoneTaskFactory.create());
    }

    @Override
    public List<NodeTask> getTasks() {
        return tasks;
    }
}
