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

import static com.intel.podm.common.types.ComposedNodeState.FAILED;
import static java.lang.String.format;

import java.util.Collection;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.dto.ComposedNodeDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.RemovalService;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.redfish.services.EntityTreeTraverser;
import com.intel.podm.business.redfish.services.assembly.NodeDisassembler;
import com.intel.podm.business.redfish.services.assembly.tasks.NodeRemovalTask;
import com.intel.podm.business.redfish.services.assembly.tasks.NodeTask;
import com.intel.podm.business.redfish.services.assembly.tasks.NodeTasksCoordinator;
import com.intel.podm.common.synchronization.TaskCoordinator;
import com.intel.podm.common.synchronization.ThrowingRunnable;
import com.intel.podm.common.types.ComposedNodeState;
import com.intel.podm.config.base.DynamicHolder;
import com.intel.podm.config.base.dto.ServiceConfig;

@Service("composedNodeRemovalService")
class ComposedNodeRemovalServiceImpl implements RemovalService<ComposedNodeDto> {
    @Autowired
    private EntityTreeTraverser traverser;

    @Autowired
    private NodeDisassembler nodeDisassembler;

    @Autowired
    private TaskCoordinator taskCoordinator;

    @Autowired
    private NodeTasksCoordinator nodeTasksCoordinator;

    private static final Logger logger = LoggerFactory.getLogger(ComposedNodeRemovalServiceImpl.class);

    @Autowired
    private DynamicHolder<ServiceConfig> config;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @SuppressWarnings({"unchecked"})
    public void perform(Context target) throws BusinessApiException, TimeoutException {
        ComposedNode composedNode = (ComposedNode) traverser.traverse(target);

        try {
			taskCoordinator.run(composedNode.getAssociatedComputerSystemUuid(), (ThrowingRunnable) () -> {
			    ComposedNodeState composedNodeState = composedNode.getComposedNodeState();
			    Collection<NodeTask> tasks = nodeDisassembler.getDisassemblyTasks(composedNode.getTheId());
			    for (NodeTask task : tasks) {
			        taskRunnerWrapper(task, composedNodeState);
			    }
			    nodeTasksCoordinator.removeAllTasks(target.getId());
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private void taskRunnerWrapper(NodeTask task, ComposedNodeState composedNodeState) throws TimeoutException {
        try {
            taskCoordinator.run(task.getServiceUuid(), task);
        } catch (RuntimeException e) {
            if (FAILED.equals(composedNodeState)) {
                logger.error(format("Error while running task %s", task), e);
                if (task instanceof NodeRemovalTask) {
                    logger.info(format("Invoking forced deallocate for task %s.", task));
                    ((NodeRemovalTask) task).deallocate();
                }
            } else {
                throw e;
            }
        }
    }
}
