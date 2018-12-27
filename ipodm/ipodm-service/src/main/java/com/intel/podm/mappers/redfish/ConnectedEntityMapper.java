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

package com.intel.podm.mappers.redfish;

import com.intel.podm.business.entities.redfish.ConnectedEntity;
import com.intel.podm.business.entities.redfish.embeddables.PciId;
import com.intel.podm.client.resources.redfish.ConnectedEntityResource;
import com.intel.podm.mappers.EntityMapper;
import com.intel.podm.mappers.subresources.IdentifierMapper;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConnectedEntityMapper extends EntityMapper<ConnectedEntityResource, ConnectedEntity> {
    @Autowired
    private PciIdMapper pciIdMapper;

    @Autowired
    private IdentifierMapper identifierMapper;

    protected ConnectedEntityMapper() {
        super(ConnectedEntityResource.class, ConnectedEntity.class);
    }

    @Override
    public void performNotAutomatedMapping(ConnectedEntityResource sourceConnectedEntity, ConnectedEntity targetConnectedEntity) {
        super.performNotAutomatedMapping(source, target);
        if (sourceConnectedEntity.getEntityPciId() != null) {
            PciId pciId = new PciId();
            pciIdMapper.map(sourceConnectedEntity.getEntityPciId(), pciId);
            targetConnectedEntity.setPciId(pciId);
        }

        identifierMapper.map(sourceConnectedEntity.getIdentifiers(), targetConnectedEntity.getIdentifiers(), targetConnectedEntity::addIdentifier);
    }
}
