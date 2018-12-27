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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.ContextResolvingException;
import com.inspur.podm.api.business.services.context.Context;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.Drive;



@Component
public class PcieDriveAttacher {
    @Autowired
    private EntityTreeTraverser traverser;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void attachPcieDriveToNode(Context composedNodeContext, Context pcieDriveContext) throws ContextResolvingException {
        ComposedNode composedNode = (ComposedNode) traverser.traverse(composedNodeContext);
        Drive drive = (Drive) traverser.traverse(pcieDriveContext);

        composedNode.addDrive(drive);
        composedNode.incrementNumberOfRequestedDrives();
        drive.getMetadata().setAllocated(true);
    }
}
