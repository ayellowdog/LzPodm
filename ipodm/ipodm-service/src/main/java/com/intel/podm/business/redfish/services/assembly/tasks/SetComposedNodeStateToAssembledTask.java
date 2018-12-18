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

import static com.intel.podm.common.types.ComposedNodeState.ASSEMBLED;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.dao.GenericDao;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.redfish.services.allocation.ComposedNodeStateChanger;
//import com.intel.podm.common.enterprise.utils.logger.TimeMeasured;

@Component
public class SetComposedNodeStateToAssembledTask extends NodeTask {
    @Autowired
    private GenericDao genericDao;

    @Autowired
    private ComposedNodeStateChanger composedNodeStateChanger;


    @Override
//    @TimeMeasured(tag = "[AssemblyTask]")
    public void run() {
        composedNodeStateChanger.change(nodeId, ASSEMBLED);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID getServiceUuid() {
        return getAssociatedComputeServiceUuid(genericDao.find(ComposedNode.class, nodeId));
    }
}
