package com.inspur.podm.service.service.redfish.impl;

import static com.inspur.podm.api.business.services.context.SingletonContext.singletonContextOf;
import static com.inspur.podm.common.intel.types.redfish.ResourceNames.CHASSIS_RESOURCE_NAME;
import static com.inspur.podm.common.intel.types.redfish.ResourceNames.COMPOSED_NODES_RESOURCE_NAME;
import static com.inspur.podm.common.intel.types.redfish.ResourceNames.COMPUTER_SYSTEM_RESOURCE_NAME;
import static com.inspur.podm.common.intel.types.redfish.ResourceNames.ETHERNET_SWITCHES_RESOURCE_NAME;
import static com.inspur.podm.common.intel.types.redfish.ResourceNames.EVENT_SERVICE_RESOURCE_NAME;
import static com.inspur.podm.common.intel.types.redfish.ResourceNames.FABRIC_RESOURCE_NAME;
import static com.inspur.podm.common.intel.types.redfish.ResourceNames.MANAGERS_RESOURCE_NAME;
import static com.inspur.podm.common.intel.types.redfish.ResourceNames.STORAGE_SERVICES_RESOURCE_NAME;
import static com.inspur.podm.common.intel.types.redfish.ResourceNames.TELEMETRY_SERVICE_RESOURCE_NAME;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.inspur.podm.api.business.dto.redfish.ServiceRootContext;
import com.inspur.podm.api.business.dto.redfish.ServiceRootDto;
import com.inspur.podm.api.business.services.redfish.ServiceRootService;

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
        dto.setSystems(singletonContextOf(context, COMPUTER_SYSTEM_RESOURCE_NAME));
        dto.setChassis(singletonContextOf(context, CHASSIS_RESOURCE_NAME));
        dto.setManagers(singletonContextOf(context, MANAGERS_RESOURCE_NAME));
        dto.setEventService(singletonContextOf(context, EVENT_SERVICE_RESOURCE_NAME));
        dto.setFabrics(singletonContextOf(context, FABRIC_RESOURCE_NAME));
        dto.setStorageServices(singletonContextOf(context, STORAGE_SERVICES_RESOURCE_NAME));
        dto.setTelemetryService(singletonContextOf(context, TELEMETRY_SERVICE_RESOURCE_NAME));

        dto.setUnknownOems(emptyList());
        ServiceRootDto.Oem.RackScaleOem rackScaleOem = dto.getOem().getRackScaleOem();
        rackScaleOem.setComposedNodes(singletonContextOf(context, COMPOSED_NODES_RESOURCE_NAME));
        rackScaleOem.setEthernetSwitches(singletonContextOf(context, ETHERNET_SWITCHES_RESOURCE_NAME));
        rackScaleOem.setApiVersion(RACKSCALE_API_VERSION);

        return dto;
    }

}

