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

package com.intel.podm.discovery.external.finalizers;


import static com.intel.podm.common.types.ServiceType.PSME;
import static com.intel.podm.common.utils.Collections.filterByType;
//import static javax.transaction.Transactional.TxType.MANDATORY;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.intel.podm.business.entities.redfish.Chassis;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.DiscoverableEntity;
import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.business.entities.redfish.base.ComposableAsset;

//@Dependent
@Component
public class PsmeDiscoveryFinalizer extends ServiceTypeSpecializedDiscoveryFinalizer {
    @Autowired
    private ComposableAssetsDiscoveryListener composableAssetsDiscoveryListener;

    @Autowired
    private EndpointLinker endpointLinker;

    @Autowired
    private ChassisHierarchyMaintainer chassisHierarchyMaintainer;

    public PsmeDiscoveryFinalizer() {
        super(PSME);
    }

    @Override
//    @Transactional(MANDATORY)
    @org.springframework.transaction.annotation.Transactional
    public void finalize(Set<DiscoverableEntity> discoveredEntities, ExternalService service) {
        chassisHierarchyMaintainer.maintain(filterByType(discoveredEntities, Chassis.class));
        endpointLinker.linkSystemToRelatedEndpoint(filterByType(discoveredEntities, ComputerSystem.class));
        composableAssetsDiscoveryListener.updateRelatedComposedNodes(filterByType(discoveredEntities, ComposableAsset.class));
    }
}
