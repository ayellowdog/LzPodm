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

package com.intel.podm.discovery;

import static com.intel.podm.common.types.ServiceType.INBAND;
import static com.intel.podm.common.types.ServiceType.PSME;
import static com.intel.podm.common.types.ServiceType.RMM;
import static com.intel.podm.common.types.ServiceType.RSS;
import static com.intel.podm.common.types.State.IN_TEST;
import static java.util.EnumSet.of;
import static java.util.stream.Collectors.toSet;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.dao.ExternalServiceDao;
import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.common.types.ServiceType;
import com.intel.podm.common.types.Status;

//@Singleton
//@Startup
//@Component
//@DependsOn({"ResourceProvider", "PodStartupDiscovery"})
public class DiscoveryStartup {
	private static final Logger logger = LoggerFactory.getLogger(DiscoveryStartup.class);

    @Autowired
    private DiscoverablesDelegalizer delegalizer;

    @Autowired
    private ServiceExplorer serviceExplorer;

    @PostConstruct
    private void initialize() {
        logger.debug("Sanitizing existing external services");
        Set<UUID> sanitizedServicesUuids = delegalizer.delegalizeExternalServicesResources();

        for (UUID serviceUuid : sanitizedServicesUuids) {
            serviceExplorer.startMonitoringOfService(serviceUuid);
        }
    }

//    @Dependent
    @Component
    public static class DiscoverablesDelegalizer {
        // Exclude DeepDiscovery LUI image type
        private static final EnumSet<ServiceType> MANAGED_TYPES = of(PSME, RSS, RMM, INBAND);

        @Autowired
        private ExternalServiceDao externalServiceDao;

//        @Transactional(REQUIRES_NEW)
        @Transactional
        public Set<UUID> delegalizeExternalServicesResources() {
            List<ExternalService> services = externalServiceDao.getExternalServicesByServicesTypes(MANAGED_TYPES);
            for (ExternalService service : services) {
                service.getOwnedLinks()
                    .stream()
                    .map(entityLink -> entityLink.getDiscoverableEntity())
                    .forEach(discoverableEntity -> discoverableEntity.setStatus(new Status(IN_TEST, null, null)));
            }

            return services.stream()
                .map(ExternalService::getUuid)
                .collect(toSet());
        }
    }
}
