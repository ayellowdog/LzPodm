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

import static java.util.Collections.emptyList;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.Violations;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.redfish.services.assembly.tasks.NodeTask;

@Component
@Transactional(propagation = Propagation.MANDATORY)
public class NoRemoteDriveStrategy implements RemoteDriveAllocationStrategy {
    @Override
    public Violations validate() {
        return new Violations();
    }

    @Override
    public Violations findResources() {
        return new Violations();
    }

    @Override
    public void allocate(ComposedNode composedNode) {
    }

    @Override
    public List<NodeTask> getTasks() {
        return emptyList();
    }
}
