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

import static com.inspur.podm.api.business.services.context.Context.contextOf;
import static com.inspur.podm.api.business.services.context.ContextType.COMPOSED_NODE;
import static com.intel.podm.common.types.ComposedNodeState.ALLOCATED;
import static javax.transaction.Transactional.TxType.NEVER;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inspur.podm.api.business.EntityOperationException;
import com.inspur.podm.api.business.RequestValidationException;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.AllocationService;
import com.inspur.podm.api.business.services.redfish.requests.RequestedNode;
import com.intel.podm.business.redfish.services.allocation.AllocationRequestProcessingException;
import com.intel.podm.business.redfish.services.allocation.ComposedNodeStateChanger;
import com.intel.podm.business.redfish.services.allocation.CompositionException;
import com.intel.podm.business.redfish.services.allocation.NodeAllocator;
import com.intel.podm.business.redfish.services.assembly.tasks.NodeTasksCoordinator;
import com.intel.podm.common.types.Id;

@Service
class AllocationServiceImpl implements AllocationService {
    @Autowired
    private NodeAllocator nodeAllocator;

    @Autowired
    private NodeTasksCoordinator nodeTasksCoordinator;

    @Autowired
    private ComposedNodeStateChanger composedNodeStateChanger;

    @Override
    @Transactional(NEVER)
    public Context allocate(RequestedNode requestedNode) throws EntityOperationException, RequestValidationException {
        String baseExceptionMessage = "Creation failed due to allocation failure: ";
        Id composedNodeId;
        try {
            composedNodeId = nodeAllocator.compose(requestedNode).getTheId();
        } catch (AllocationRequestProcessingException e) {
            throw new RequestValidationException(baseExceptionMessage + e.getMessage(), e.getViolations(), e);
        } catch (CompositionException e) {
            throw new EntityOperationException(baseExceptionMessage + e.getMessage(), e);
        }
        
//       发送http请求，先注释掉
//        try {
//            nodeTasksCoordinator.setTasksForNode(composedNodeId, nodeAllocator.getAssemblyTasks());
//        } catch (IllegalStateException e) {
//            throw new EntityOperationException(baseExceptionMessage + "Composed Node [" + composedNodeId + "] has been already allocated.", e);
//        }

        composedNodeStateChanger.change(composedNodeId, ALLOCATED);
        return contextOf(composedNodeId, COMPOSED_NODE);
    }
}
