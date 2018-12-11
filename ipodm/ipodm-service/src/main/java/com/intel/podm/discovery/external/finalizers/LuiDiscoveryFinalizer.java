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


import static com.intel.podm.common.types.ServiceType.LUI;
import static com.intel.podm.common.types.ServiceType.PSME;
import static com.intel.podm.common.utils.Collections.filterByType;
import static com.intel.podm.common.utils.Contracts.checkState;
import static com.intel.podm.common.utils.IterableHelper.single;
import static com.intel.podm.discovery.external.finalizers.DeepDiscoveryCompletedEvent.deepDiscoveryCompletedEvent;

import java.util.Collection;
import java.util.Set;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.intel.podm.business.entities.redfish.Chassis;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.DiscoverableEntity;
import com.intel.podm.business.entities.redfish.Drive;
import com.intel.podm.business.entities.redfish.ExternalService;

//@Dependent
@Component
public class LuiDiscoveryFinalizer extends ServiceTypeSpecializedDiscoveryFinalizer {
	private static final Logger logger = LoggerFactory.getLogger(LuiDiscoveryFinalizer.class);

//    @Inject
    private BeanManager beanManager;

    public LuiDiscoveryFinalizer() {
        super(LUI);
    }

    @Override
//    @Transactional(MANDATORY)
    @org.springframework.transaction.annotation.Transactional
    public void finalize(Set<DiscoverableEntity> discoveredEntities, ExternalService service) {
        finalizeDeepDiscovery(discoveredEntities);
        ComputerSystem computerSystem = retrieveDiscoveredComputerSystem(discoveredEntities);
        logger.info("Finalizing Deep discovery for ComputerSystem: {} [service: {}, path: {}]",
            computerSystem.getId(), service.getBaseUri(), computerSystem.getSourceUri());
        ExternalService externalService = computerSystem.getService();
        if (externalService == null || !PSME.equals(externalService.getServiceType())) {
            throw new IllegalStateException("ComputerSystem should be associated with single PSME external service");
        }

        attachDrivesToChassis(discoveredEntities, computerSystem);
    }

    private void finalizeDeepDiscovery(Set<DiscoverableEntity> discoveredEntities) {
        filterByType(discoveredEntities, ComputerSystem.class).stream()
            .map(ComputerSystem::getTheId)
            .forEach(id -> beanManager.fireEvent(deepDiscoveryCompletedEvent(id)));
    }

    private static ComputerSystem retrieveDiscoveredComputerSystem(Set<DiscoverableEntity> discoveredEntities) {
        Collection<ComputerSystem> computerSystems = filterByType(discoveredEntities, ComputerSystem.class);
        checkState(computerSystems.size() == 1, "LUI service should have exactly one Computer System resource (" + computerSystems.size() + " found).");

        return computerSystems.iterator().next();
    }

    private static void attachDrivesToChassis(Set<DiscoverableEntity> discoveredEntities, ComputerSystem computerSystem) {
        Chassis chassis = single(computerSystem.getChassis());
        filterByType(discoveredEntities, Drive.class).forEach(drive -> drive.setChassis(chassis));
    }
}
