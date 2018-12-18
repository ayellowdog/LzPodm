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


import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.EntityOperationException;
import com.intel.podm.business.entities.dao.GenericDao;
import com.intel.podm.business.entities.redfish.Drive;
import com.intel.podm.business.redfish.services.actions.PcieDriveActionsInvoker;
//import com.intel.podm.common.enterprise.utils.logger.TimeMeasured;
import com.intel.podm.common.types.Id;

@Component
public class SecureErasePcieDriveTask extends NodeTask {
    @Autowired
    private GenericDao genericDao;

    @Autowired
    private PcieDriveActionsInvoker pcieDriveActionsInvoker;

    private static final Logger logger = LoggerFactory.getLogger(SecureErasePcieDriveTask.class);
    
    private Id pcieDriveId;

    @Override
//    @TimeMeasured(tag = "[AssemblyTask]")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void run() {
        Drive drive = genericDao.find(Drive.class, pcieDriveId);
        try {
            pcieDriveActionsInvoker.secureErase(drive);
        } catch (EntityOperationException e) {
            logger.error("SecureErase action failed: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID getServiceUuid() {
        return genericDao.find(Drive.class, pcieDriveId).getService().getUuid();
    }

    public SecureErasePcieDriveTask setPcieDriveId(Id pcieDriveId) {
        this.pcieDriveId = pcieDriveId;
        return this;
    }
}
