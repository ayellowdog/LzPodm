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

package com.inspur.podm.common.persistence.entity;


import static com.inspur.podm.common.intel.types.DeepDiscoveryState.RUNNING;
import static com.inspur.podm.common.intel.types.DeepDiscoveryState.WAITING_TO_START;
import static java.util.Arrays.stream;

import java.util.UUID;

import com.inspur.podm.common.intel.types.DeepDiscoveryState;
import com.inspur.podm.common.persistence.BaseEntity;

//@javax.persistence.Entity
//@Table(name = "computer_system_metadata")
public class ComputerSystemMetadata extends BaseEntity {
/** @Fields serialVersionUID: TODO 功能描述  */
	private static final long serialVersionUID = 1L;

	//    @Column(name = "allocated")
    private boolean allocated;

//    @Column(name = "deep_discovery_state")
//    @Enumerated(STRING)
    private DeepDiscoveryState deepDiscoveryState;

//    @Column(name = "task_uuid")
    private UUID taskUuid;

    public boolean isAllocated() {
        return allocated;
    }

    public void setAllocated(boolean allocated) {
        this.allocated = allocated;
    }

    public DeepDiscoveryState getDeepDiscoveryState() {
        return deepDiscoveryState;
    }

    public void setDeepDiscoveryState(DeepDiscoveryState deepDiscoveryState) {
        this.deepDiscoveryState = deepDiscoveryState;
    }

    public UUID getTaskUuid() {
        return taskUuid;
    }

    public void setTaskUuid(UUID taskUuid) {
        this.taskUuid = taskUuid;
    }

    public boolean isInAnyOfStates(DeepDiscoveryState... states) {
        DeepDiscoveryState actualDeepDiscoveryState = getDeepDiscoveryState();
        return actualDeepDiscoveryState != null
            && stream(states).anyMatch(expectedDeepDiscoveryState -> actualDeepDiscoveryState == expectedDeepDiscoveryState);
    }

    public boolean isBeingDeepDiscovered() {
        return isInAnyOfStates(WAITING_TO_START, RUNNING);
    }

    @Override
    public void preRemove() {
    }

    @Override
    public boolean containedBy(BaseEntity possibleParent) {
        return false;
    }
}
