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

package com.intel.podm.discovery.external.event;

import static javax.ws.rs.core.UriBuilder.fromUri;

import java.net.URI;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.redfish.ExternalService;
//import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.client.events.EventServiceDefinition;
import com.intel.podm.common.types.ServiceType;
import com.intel.podm.config.base.Config;
import com.intel.podm.config.base.ConfigProvider;
import com.intel.podm.config.base.Holder;
import com.intel.podm.config.base.dto.EventsConfig;
import com.intel.podm.config.base.dto.EventsConfig.EventConfiguration;
import com.intel.podm.config.base.dto.ServiceConfig;
import com.intel.podm.config.base.dto.ServiceConnectionConfig;
import com.intel.podm.discovery.external.ExternalServiceRepository;

//@ApplicationScoped
@Component
class EventServiceDefinitionFactory {
//    @Inject
    @Config
//    @Autowir
@Resource(name="podmConfigProvider")
//    private Holder<EventsConfig> eventsConfig;
    private ConfigProvider eventsConfig;

//    @Inject
//    @Autowired
    @Config
    @Resource(name="podmConfigProvider")
//    private Holder<ServiceConfig> serviceConfig;
    private ConfigProvider serviceConfig;

//    @Inject
//    @Autowired
    @Config
    @Resource(name="podmConfigProvider")
//    private Holder<ServiceConnectionConfig> connectionConfig;
    private ConfigProvider connectionConfig;

//    @Inject
    @Autowired
    private ExternalServiceRepository repository;

    private UUID podManagerServiceUuid;

    @PostConstruct
    public void initialize() {
        podManagerServiceUuid = serviceConfig.get(ServiceConfig.class).getUuid();
    }

//    @Transactional(REQUIRES_NEW)
    @Transactional(propagation = Propagation.REQUIRED)
    EventServiceDefinition create(UUID serviceUuid) {
        ExternalService service = repository.find(serviceUuid);
        URI podManagerEventServiceUri = buildPodManagerEventServiceUri(service.getServiceType(), service.getUuid());
        return new EventServiceDefinition(podManagerEventServiceUri, service.getBaseUri(), podManagerServiceUuid);
    }

    private URI buildPodManagerEventServiceUri(ServiceType serviceType, UUID serviceUuid) {
        EventsConfig config = this.eventsConfig.get(EventsConfig.class);
        EventConfiguration serviceTypeEventConfig = config.getEventConfigForServiceType(serviceType);
        boolean sslEnabled = connectionConfig.get(ServiceConnectionConfig.class).getConnectionSecurity().isSslEnabledForServicesOfType(serviceType);
        URI baseEndpoint = sslEnabled ? serviceTypeEventConfig.getSecurePodManagerEventReceivingEndpoint()
            : serviceTypeEventConfig.getDefaultPodManagerEventReceivingEndpoint();

        return fromUri(baseEndpoint).path("/" + serviceUuid).build();
    }
}
