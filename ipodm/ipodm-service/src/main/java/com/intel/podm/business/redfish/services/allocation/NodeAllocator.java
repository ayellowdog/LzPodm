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

package com.intel.podm.business.redfish.services.allocation;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.OptimisticLockException;
import javax.transaction.RollbackException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.services.redfish.requests.RequestedNode;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.redfish.services.allocation.strategy.AllocationStrategy;
import com.intel.podm.business.redfish.services.allocation.strategy.AllocationStrategyFactory;
import com.intel.podm.business.redfish.services.allocation.strategy.ResourceFinderException;
import com.intel.podm.business.redfish.services.assembly.tasks.ContinuouslyOverrideBootSourceTask;
import com.intel.podm.business.redfish.services.assembly.tasks.NodeTask;
import com.intel.podm.business.redfish.services.assembly.tasks.PowerOffTask;
import com.intel.podm.business.redfish.services.assembly.tasks.SetComposedNodeStateToAssembledTask;
import com.intel.podm.common.enterprise.utils.beans.BeanFactory;

@Component
public class NodeAllocator {
    @Autowired
    private AllocationStrategyFactory allocationStrategyFactory;

    @Autowired
    private BeanFactory beanFactory;
    
    private List<NodeTask> assemblyTasks = new ArrayList<>();

    @Retryable(value= {RollbackException.class, OptimisticLockException.class},maxAttempts = 3,backoff = @Backoff(delay = 100l,multiplier = 1))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ComposedNode compose(RequestedNode requestedNode) throws AllocationRequestProcessingException, ResourceFinderException {
        AllocationStrategy allocationStrategy = allocationStrategyFactory.create(requestedNode);
        allocationStrategy.validate();

        ComputerSystem computerSystemResource = allocationStrategy.findComputerSystemResource();
        ComposedNode node = allocationStrategy.allocateWithComputerSystem(computerSystemResource);

        List<NodeTask> tasks = new ArrayList<>(allocationStrategy.getTasks());
        tasks.addAll(createDefaultTasks());
        this.assemblyTasks = tasks;

        return node;
    }

    private List<NodeTask> createDefaultTasks() {
        return asList(
            beanFactory.create(PowerOffTask.class),
            beanFactory.create(ContinuouslyOverrideBootSourceTask.class),
            beanFactory.create(SetComposedNodeStateToAssembledTask.class)
        );
    }

    public List<NodeTask> getAssemblyTasks() {
        return assemblyTasks;
    }
}
