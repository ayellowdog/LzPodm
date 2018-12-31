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

package com.intel.podm.discovery.external;

import com.intel.podm.business.entities.redfish.ExternalService;
//import com.intel.podm.common.enterprise.utils.logger.ServiceLifecycle;
import com.intel.podm.common.logger.ServiceLifecycleLogger;
import com.intel.podm.common.types.ServiceType;
import com.intel.podm.common.types.discovery.ServiceEndpoint;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Objects;

import static com.intel.podm.common.types.ServiceType.INBAND;
import static com.intel.podm.common.types.ServiceType.LUI;
import static java.util.EnumSet.of;
import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

//@Dependent
@Component
public class ExternalServiceUpdater {
    private static final EnumSet<ServiceType> INBAND_TYPES = of(INBAND, LUI);

//    @Inject
//    @ServiceLifecycle
//    private ServiceLifecycleLogger logger;

    @Autowired
    private ExternalServiceRepository repository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateExternalService(ServiceEndpoint serviceEndpoint) {
        ExternalService service = repository.findOrNull(serviceEndpoint.getServiceUuid());
        if (service == null) {
            service = repository.create(serviceEndpoint);
//            logger.lifecycleInfo("New service {} discovered.", service);
        }

        if (!Objects.equals(service.getBaseUri(), serviceEndpoint.getEndpointUri())) {
//            logger.lifecycleInfo(
//                "Service's URI for {} was updated to {}",
//                service, serviceEndpoint.getEndpointUri()
//            );
        }

        service.setEventingAvailable(isEventingAvailableForServiceType(serviceEndpoint.getServiceType()));
        service.setComplementaryDataSource(INBAND_TYPES.contains(serviceEndpoint.getServiceType()));
        service.setBaseUri(serviceEndpoint.getEndpointUri());
    }

    private boolean isEventingAvailableForServiceType(ServiceType serviceType) {
        return !LUI.equals(serviceType);
    }
}
