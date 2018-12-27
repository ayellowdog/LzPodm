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

import static com.intel.podm.common.utils.Converters.convertBytesToGib;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.Violations;
import com.inspur.podm.api.business.services.redfish.requests.RequestedNode.RemoteDrive;
import com.intel.podm.business.entities.EntityNotFoundException;
import com.intel.podm.business.entities.dao.GenericDao;
import com.intel.podm.business.entities.redfish.StoragePool;
import com.intel.podm.business.entities.redfish.Volume;
import com.intel.podm.business.redfish.services.helpers.VolumeHelper;

@Component("masterBasedNewDriveAllocationContextDescriber")
public class MasterBasedNewDriveAllocationContextDescriber implements RemoteDriveAllocationContextDescriber {
    @Autowired
    private GenericDao genericDao;

    @Autowired
    private VolumeHelper volumeHelper;

    @Transactional(propagation = Propagation.MANDATORY)
    public RemoteDriveAllocationContextDescriptor describe(RemoteDrive remoteDrive) {
        RemoteDriveAllocationContextDescriptor descriptor = new RemoteDriveAllocationContextDescriptor();

        Volume masterDrive = getMasterDrive(remoteDrive.getMaster());

        if (validateStoragePool(descriptor, masterDrive)) {
            return descriptor;
        }

        descriptor.setMasterUri(masterDrive.getSourceUri());
        descriptor.setStoragePoolId(masterDrive.getStoragePool().getId());
        descriptor.setStorageServiceId(masterDrive.getStorageService().getId());
        descriptor.setCapacity(getCapacity(remoteDrive, masterDrive));
        descriptor.setProtocol(volumeHelper.retrieveProtocolFromVolume(masterDrive));
        descriptor.setReplicaType(remoteDrive.getMaster().getType());

        return descriptor;
    }

    private boolean validateStoragePool(RemoteDriveAllocationContextDescriptor resource, Volume masterDrive) {
        Violations violations = new Violations();
        StoragePool storagePool = masterDrive.getStoragePool();

        if (storagePool == null) {
            violations.addViolation("Storage pool was not found on Volume.");
            resource.setViolations(violations);
            return true;
        }

        if (!hasRequiredSpace(storagePool, masterDrive.getCapacityBytes())) {
            violations.addViolation("Storage pool with sufficient space was not found.");
            resource.setViolations(violations);
            return true;
        }

        return false;
    }

    private BigDecimal getCapacity(RemoteDrive remoteDrive, Volume masterDrive) {
        if (remoteDrive.getCapacityGib() != null) {
            return remoteDrive.getCapacityGib();
        }

        return convertBytesToGib(masterDrive.getCapacityBytes());
    }

    private Volume getMasterDrive(RemoteDrive.MasterDrive masterDrive) {
        try {
            return genericDao.find(Volume.class, masterDrive.getResourceContext().getId());
        } catch (EntityNotFoundException e) {
            throw new IllegalStateException("Provided master drive no longer exist.");
        }
    }
}
