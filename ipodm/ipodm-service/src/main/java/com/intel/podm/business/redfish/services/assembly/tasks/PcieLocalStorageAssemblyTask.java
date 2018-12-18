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

import static com.intel.podm.common.utils.Contracts.requires;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.EntityOperationException;
import com.intel.podm.business.entities.dao.GenericDao;
import com.intel.podm.business.entities.redfish.Endpoint;
import com.intel.podm.business.entities.redfish.Zone;
import com.intel.podm.business.redfish.services.actions.PcieZoneActionsInvoker;
//import com.intel.podm.common.enterprise.utils.logger.TimeMeasured;
import com.intel.podm.common.types.Id;

@Component
public class PcieLocalStorageAssemblyTask extends NodeTask {
    private Id zoneId;
    private Id endpointId;

    @Autowired
    private PcieZoneActionsInvoker pcieZoneActionsInvoker;

    @Autowired
    private GenericDao genericDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @TimeMeasured(tag = "[AssemblyTask]")
    public void run() {
        final String exceptionMessage = "Null value is not allowed for this method.";
        requires(zoneId != null, exceptionMessage);
        requires(endpointId != null, exceptionMessage);

        Zone zone = genericDao.find(Zone.class, zoneId);
        Endpoint endpoint = genericDao.find(Endpoint.class, endpointId);

        try {
            pcieZoneActionsInvoker.attachEndpoint(zone, endpoint);
        } catch (EntityOperationException e) {
            throw new RuntimeException("Updating Zone failed", e);
        }
    }

    public void setZoneId(Id zoneId) {
        this.zoneId = zoneId;
    }

    public void setEndpointId(Id endpointId) {
        this.endpointId = endpointId;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID getServiceUuid() {
        return genericDao.find(Zone.class, zoneId).getService().getUuid();
    }
}
