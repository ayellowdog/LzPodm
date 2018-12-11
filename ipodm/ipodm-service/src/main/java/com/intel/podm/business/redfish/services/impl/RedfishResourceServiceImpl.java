package com.intel.podm.business.redfish.services.impl;

import static com.inspur.podm.api.business.services.context.SingletonContext.singletonContextOf;
import static com.intel.podm.common.types.redfish.ResourceNames.CHASSIS_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.COMPOSED_NODES_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.COMPUTER_SYSTEM_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.ETHERNET_SWITCHES_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.EVENT_SERVICE_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.FABRIC_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.MANAGERS_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.STORAGE_SERVICES_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.TELEMETRY_SERVICE_RESOURCE_NAME;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;

import java.net.URI;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspur.podm.api.business.dto.redfish.ServiceRootContext;
import com.inspur.podm.api.business.dto.redfish.ServiceRootDto;
import com.inspur.podm.api.business.services.redfish.ServiceRootService;
import com.inspur.podm.api.business.services.redfish.odataid.ODataId;

@Service
public class RedfishResourceServiceImpl implements ServiceRootService {
    private static final String REDFISH_SERVICE_VERSION = "1.1.0";
    private static final String RACKSCALE_API_VERSION = "2.3.0";
    private UUID uuid = randomUUID();


    @Override
    public ServiceRootDto getServiceRoot() {
        ServiceRootDto dto = new ServiceRootDto();
        dto.setId("ServiceRoot");
        dto.setName("Service root");
        dto.setDescription("POD Manager Service root");
        dto.setRedfishVersion(REDFISH_SERVICE_VERSION);
        dto.setUuid(uuid);

        ServiceRootContext context = new ServiceRootContext();
        String rootOdata = context.asOdataId() + "/";
        dto.setSystems(singletonContextOf(context, COMPUTER_SYSTEM_RESOURCE_NAME, URI.create(rootOdata + COMPUTER_SYSTEM_RESOURCE_NAME)));
        dto.setChassis(singletonContextOf(context, CHASSIS_RESOURCE_NAME, URI.create(rootOdata + CHASSIS_RESOURCE_NAME)));
        dto.setManagers(singletonContextOf(context, MANAGERS_RESOURCE_NAME, URI.create(rootOdata + MANAGERS_RESOURCE_NAME)));
        dto.setEventService(singletonContextOf(context, EVENT_SERVICE_RESOURCE_NAME, URI.create(rootOdata + EVENT_SERVICE_RESOURCE_NAME)));
        dto.setFabrics(singletonContextOf(context, FABRIC_RESOURCE_NAME, URI.create(rootOdata + FABRIC_RESOURCE_NAME)));
        dto.setStorageServices(singletonContextOf(context, STORAGE_SERVICES_RESOURCE_NAME, URI.create(rootOdata + STORAGE_SERVICES_RESOURCE_NAME)));
        dto.setTelemetryService(singletonContextOf(context, TELEMETRY_SERVICE_RESOURCE_NAME, URI.create(rootOdata + TELEMETRY_SERVICE_RESOURCE_NAME)));

        dto.setUnknownOems(emptyList());
        ServiceRootDto.Oem.RackScaleOem rackScaleOem = dto.getOem().getRackScaleOem();
        rackScaleOem.setComposedNodes(singletonContextOf(context, COMPOSED_NODES_RESOURCE_NAME, URI.create(rootOdata + COMPOSED_NODES_RESOURCE_NAME)));
        rackScaleOem.setEthernetSwitches(singletonContextOf(context, ETHERNET_SWITCHES_RESOURCE_NAME, URI.create(rootOdata + ETHERNET_SWITCHES_RESOURCE_NAME)));
        rackScaleOem.setApiVersion(RACKSCALE_API_VERSION);

        return dto;
    }
    
}

