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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.EntityOperationException;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.ActionService;
import com.inspur.podm.api.business.services.redfish.requests.AssemblyRequest;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.redfish.services.EntityTreeTraverser;
import com.intel.podm.business.redfish.services.assembly.AssemblyException;
import com.intel.podm.business.redfish.services.assembly.NodeAssembler;


@Service("assemblyActionServiceImpl")
class AssemblyActionServiceImpl implements ActionService<AssemblyRequest> {
    @Autowired
    private NodeAssembler nodeAssembler;

    @Autowired
    private EntityTreeTraverser traverser;

    @Override
//    @RetryOnRollback(3)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void perform(Context target, AssemblyRequest request) throws BusinessApiException {
        ComposedNode composedNode = (ComposedNode) traverser.traverse(target);
        try {
            nodeAssembler.assemble(composedNode);
        } catch (AssemblyException e) {
            throw new EntityOperationException("Assembly failed: " + e.getMessage(), e);
        }
    }
}
