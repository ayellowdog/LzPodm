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

import static java.lang.String.format;
import static java.util.Arrays.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.EntityOperationException;
import com.intel.podm.business.entities.dao.ConnectedEntityDao;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.ConnectedEntity;
import com.intel.podm.business.entities.redfish.DiscoverableEntity;
import com.intel.podm.business.entities.redfish.Endpoint;
import com.intel.podm.business.entities.redfish.Volume;
import com.intel.podm.business.redfish.services.actions.DeleteActionInvoker;
import com.intel.podm.business.redfish.services.actions.VolumeInitializeActionInvoker;

@Component
public class VolumeDetachService {
	
    private static final Logger logger = LoggerFactory.getLogger(VolumeDetachService.class);

    @Autowired
    private VolumeInitializeActionInvoker volumeInitializeActionInvoker;

    @Autowired
    private DeleteActionInvoker deleteActionInvoker;

    @Autowired
    private ConnectedEntityDao connectedEntityDao;

    @Autowired
    private EndpointDetachService endpointDetachService;

    @Transactional(propagation = Propagation.MANDATORY)
    void detachVolume(ComposedNode composedNode, Volume volume) throws BusinessApiException {
        unlink(composedNode, volume);
        detachEndpointsFromVolume(volume);
        deallocate(volume);

        if (volume.isErasePossible()) {
            try {
                volumeInitializeActionInvoker.initialize(volume, null);
            } catch (EntityOperationException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    private void detachEndpointsFromVolume(Volume volume) throws BusinessApiException {
        for (Endpoint endpoint : volume.getEndpoints()) {
            ConnectedEntity connectedEntity = connectedEntityDao.getConnectedEntity(endpoint, volume);
            if (endpoint.getConnectedEntities().size() == 1) {
                endpointDetachService.unlinkEndpointFromZone(endpoint);
                removeAssets(connectedEntity, endpoint);
            } else {
                endpoint.unlinkConnectedEntities(connectedEntity);
                removeAssets(connectedEntity);
            }
        }
    }

    private void deallocate(Volume volume) {
        volume.getMetadata().setAllocated(false);
    }

    private void unlink(ComposedNode node, Volume volume) {
        node.unlinkVolume(volume);
    }

    private void removeAssets(DiscoverableEntity... entities) {
        stream(entities).forEach(entity -> {
            try {
                deleteActionInvoker.delete(entity);
            } catch (EntityOperationException e) {
                logger.error(format("Delete action of '%s' failed with exception -> %s", entity.getSourceUri(), e));
            }
        });
    }
}
