/*
 * Copyright (c) 2018 Intel Corporation
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

package com.intel.podm.business.redfish.services.assembly.tasks.volume;


import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.StorageService;
import com.intel.podm.business.entities.redfish.Volume;
import com.intel.podm.client.WebClientRequestException;
//import com.intel.podm.common.enterprise.utils.logger.TimeMeasured;
import com.intel.podm.discovery.external.partial.StorageServiceVolumeObtainer;

@Component
public class ObtainNewVolumeTask extends NewVolumeTask {
	
    private static final Logger logger = LoggerFactory.getLogger(ObtainNewVolumeTask.class);

    @Autowired
    private StorageServiceVolumeObtainer volumeObtainer;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @TimeMeasured(tag = "[AssemblyTask]")
    public void run() {
        try {
            ComposedNode composedNode = genericDao.find(ComposedNode.class, nodeId);
            StorageService storageService = getStorageServiceFromResourceDescriptor();
            URI relativeVolumeUri = URI.create(getNewRemoteVolumeUri().getPath());
            Volume volume = volumeObtainer.discoverStorageServiceVolume(storageService, relativeVolumeUri);
            volume.getMetadata().setAllocated(true);
            composedNode.addVolume(volume);
            composedNode.setAssociatedStorageServiceUuid(volume.getService().getUuid());
            composedNode.addAssociatedVolumeIdentifiers(volume.getIdentifiers());
        } catch (WebClientRequestException e) {
            logger.error("Volume obtain failed for Node: {}, details: {}", nodeId, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
