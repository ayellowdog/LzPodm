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

package com.intel.podm.business.redfish.services.allocation.strategy.matcher;

import static com.intel.podm.common.utils.Converters.convertGibToBytes;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.Violations;
import com.inspur.podm.api.business.services.redfish.requests.RequestedNode.RemoteDrive;
import com.intel.podm.business.entities.dao.GenericDao;
import com.intel.podm.business.entities.redfish.StoragePool;
import com.intel.podm.business.redfish.services.allocation.strategy.RemoteDriveAllocationContextDescriber;
import com.intel.podm.business.redfish.services.allocation.strategy.RemoteDriveAllocationContextDescriptor;

@Component("zdDriveAllocationContextDescriber")
public class NewDriveAllocationContextDescriber implements RemoteDriveAllocationContextDescriber {
    @Autowired
    private GenericDao genericDao;

    @Transactional(propagation = Propagation.MANDATORY)
    public RemoteDriveAllocationContextDescriptor describe(RemoteDrive remoteDrive) {
        RemoteDriveAllocationContextDescriptor resource = new RemoteDriveAllocationContextDescriptor();
        Violations violations = new Violations();

        Optional<StoragePool> pool = getAnyPoolMatchingRequest(remoteDrive);

        if (!pool.isPresent()) {
            violations.addViolation("There is no Storage pool with sufficient space available.");
            resource.setViolations(violations);

            return resource;
        }

        StoragePool selectedPool = pool.get();

        resource.setStoragePoolId(selectedPool.getId());
        resource.setStoragePoolUri(selectedPool.getSourceUri());
        resource.setStorageServiceId(selectedPool.getStorageService().getId());
        resource.setCapacity(remoteDrive.getCapacityGib());
        resource.setViolations(violations);
        resource.setProtocol(remoteDrive.getProtocol());

        return resource;
    }

    private Optional<StoragePool> getAnyPoolMatchingRequest(RemoteDrive remoteDrive) {
        BigDecimal requestedSize = convertGibToBytes(remoteDrive.getCapacityGib());

        return genericDao.findAll(StoragePool.class).stream()
            .filter(storagePool -> hasRequiredSpace(storagePool, requestedSize))
            .filter(storagePool -> remoteDrive.getProtocol().equals(storagePool.getProtocol()))
            .findFirst();
    }
}
