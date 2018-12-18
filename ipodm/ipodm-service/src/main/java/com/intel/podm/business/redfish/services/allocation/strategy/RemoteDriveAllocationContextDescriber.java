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

package com.intel.podm.business.redfish.services.allocation.strategy;

import java.math.BigDecimal;

import com.inspur.podm.api.business.services.redfish.requests.RequestedNode.RemoteDrive;
import com.intel.podm.business.entities.redfish.StoragePool;

public interface RemoteDriveAllocationContextDescriber {
    RemoteDriveAllocationContextDescriptor describe(RemoteDrive remoteDrive);

    default boolean hasRequiredSpace(StoragePool storagePool, BigDecimal requiredSpace) {
        return requiredSpace.compareTo(storagePool.getFreeSpace()) <= 0;
    }
}
