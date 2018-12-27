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


import static com.intel.podm.common.types.ServiceType.RSS;
import static com.intel.podm.common.utils.Collections.filterByType;
import static javax.transaction.Transactional.TxType.MANDATORY;

import java.util.Set;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.redfish.Chassis;
import com.intel.podm.business.entities.redfish.DiscoverableEntity;
import com.intel.podm.business.entities.redfish.Endpoint;
import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.business.entities.redfish.base.ComposableAsset;

//@Dependent
@Component
@Lazy
public class RssDiscoveryFinalizer extends ServiceTypeSpecializedDiscoveryFinalizer {
    @Autowired
    private ComposableAssetsDiscoveryListener composableAssetsDiscoveryListener;

    @Autowired
    private EndpointLinker endpointLinker;

    @Autowired
    private ChassisHierarchyMaintainer chassisHierarchyMaintainer;

    public RssDiscoveryFinalizer() {
        super(RSS);
    }

    @Override
//    @Transactional(MANDATORY)
    @Transactional(propagation = Propagation.MANDATORY)
    public void finalize(Set<DiscoverableEntity> discoveredEntities, ExternalService service) {
        chassisHierarchyMaintainer.maintain(filterByType(discoveredEntities, Chassis.class));
        endpointLinker.linkEndpointToRelatedSystem(filterByType(discoveredEntities, Endpoint.class));
        composableAssetsDiscoveryListener.updateRelatedComposedNodes(filterByType(discoveredEntities, ComposableAsset.class));
    }
}
