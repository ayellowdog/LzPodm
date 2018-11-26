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

package com.inspur.podm.service.service.redfish;

import com.inspur.podm.common.persistence.entity.Chassis;
import com.inspur.podm.common.persistence.entity.ComposedNode;
import com.inspur.podm.common.persistence.entity.ComputerSystem;
import com.inspur.podm.common.persistence.entity.ComputerSystemMetrics;
import com.inspur.podm.common.persistence.entity.Drive;
import com.inspur.podm.common.persistence.entity.DriveMetrics;
import com.inspur.podm.common.persistence.entity.Endpoint;
import com.inspur.podm.common.persistence.entity.EthernetInterface;
import com.inspur.podm.common.persistence.entity.EthernetSwitch;
import com.inspur.podm.common.persistence.entity.EthernetSwitchAcl;
import com.inspur.podm.common.persistence.entity.EthernetSwitchAclRule;
import com.inspur.podm.common.persistence.entity.EthernetSwitchMetrics;
import com.inspur.podm.common.persistence.entity.EthernetSwitchPort;
import com.inspur.podm.common.persistence.entity.EthernetSwitchPortMetrics;
import com.inspur.podm.common.persistence.entity.EthernetSwitchPortVlan;
import com.inspur.podm.common.persistence.entity.EthernetSwitchStaticMac;
import com.inspur.podm.common.persistence.entity.EventSubscription;
import com.inspur.podm.common.persistence.entity.Fabric;
import com.inspur.podm.common.persistence.entity.Manager;
import com.inspur.podm.common.persistence.entity.Memory;
import com.inspur.podm.common.persistence.entity.MemoryMetrics;
import com.inspur.podm.common.persistence.entity.MetricDefinition;
import com.inspur.podm.common.persistence.entity.MetricReportDefinition;
import com.inspur.podm.common.persistence.entity.NetworkDeviceFunction;
import com.inspur.podm.common.persistence.entity.NetworkInterface;
import com.inspur.podm.common.persistence.entity.NetworkProtocol;
import com.inspur.podm.common.persistence.entity.PcieDevice;
import com.inspur.podm.common.persistence.entity.PcieDeviceFunction;
import com.inspur.podm.common.persistence.entity.Port;
import com.inspur.podm.common.persistence.entity.PortMetrics;
import com.inspur.podm.common.persistence.entity.Power;
import com.inspur.podm.common.persistence.entity.PowerControl;
import com.inspur.podm.common.persistence.entity.PowerSupply;
import com.inspur.podm.common.persistence.entity.PowerVoltage;
import com.inspur.podm.common.persistence.entity.Processor;
import com.inspur.podm.common.persistence.entity.ProcessorMetrics;
import com.inspur.podm.common.persistence.entity.Redundancy;
import com.inspur.podm.common.persistence.entity.SimpleStorage;
import com.inspur.podm.common.persistence.entity.Storage;
import com.inspur.podm.common.persistence.entity.StoragePool;
import com.inspur.podm.common.persistence.entity.StorageService;
import com.inspur.podm.common.persistence.entity.Switch;
import com.inspur.podm.common.persistence.entity.Thermal;
import com.inspur.podm.common.persistence.entity.ThermalFan;
import com.inspur.podm.common.persistence.entity.ThermalTemperature;
import com.inspur.podm.common.persistence.entity.Volume;
import com.inspur.podm.common.persistence.entity.VolumeMetrics;
import com.inspur.podm.common.persistence.entity.Zone;
import com.inspur.podm.common.persistence.base.Entity;
import com.inspur.podm.api.business.services.context.ContextType;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.inspur.podm.api.business.services.context.ContextType.CHASSIS;
import static com.inspur.podm.api.business.services.context.ContextType.COMPOSED_NODE;
import static com.inspur.podm.api.business.services.context.ContextType.COMPUTER_SYSTEM;
import static com.inspur.podm.api.business.services.context.ContextType.COMPUTER_SYSTEM_METRICS;
import static com.inspur.podm.api.business.services.context.ContextType.DRIVE;
import static com.inspur.podm.api.business.services.context.ContextType.DRIVE_METRICS;
import static com.inspur.podm.api.business.services.context.ContextType.ENDPOINT;
import static com.inspur.podm.api.business.services.context.ContextType.ETHERNET_INTERFACE;
import static com.inspur.podm.api.business.services.context.ContextType.ETHERNET_SWITCH;
import static com.inspur.podm.api.business.services.context.ContextType.ETHERNET_SWITCH_ACL;
import static com.inspur.podm.api.business.services.context.ContextType.ETHERNET_SWITCH_ACL_RULE;
import static com.inspur.podm.api.business.services.context.ContextType.ETHERNET_SWITCH_METRICS;
import static com.inspur.podm.api.business.services.context.ContextType.ETHERNET_SWITCH_PORT;
import static com.inspur.podm.api.business.services.context.ContextType.ETHERNET_SWITCH_PORT_METRICS;
import static com.inspur.podm.api.business.services.context.ContextType.ETHERNET_SWITCH_PORT_VLAN;
import static com.inspur.podm.api.business.services.context.ContextType.ETHERNET_SWITCH_STATIC_MAC;
import static com.inspur.podm.api.business.services.context.ContextType.EVENT_SUBSCRIPTION;
import static com.inspur.podm.api.business.services.context.ContextType.FABRIC;
import static com.inspur.podm.api.business.services.context.ContextType.MANAGER;
import static com.inspur.podm.api.business.services.context.ContextType.MEMORY;
import static com.inspur.podm.api.business.services.context.ContextType.MEMORY_METRICS;
import static com.inspur.podm.api.business.services.context.ContextType.METRIC_DEFINITION;
import static com.inspur.podm.api.business.services.context.ContextType.METRIC_REPORT_DEFINITION;
import static com.inspur.podm.api.business.services.context.ContextType.NETWORK_DEVICE_FUNCTION;
import static com.inspur.podm.api.business.services.context.ContextType.NETWORK_INTERFACE;
import static com.inspur.podm.api.business.services.context.ContextType.NETWORK_PROTOCOL;
import static com.inspur.podm.api.business.services.context.ContextType.PCIE_DEVICE;
import static com.inspur.podm.api.business.services.context.ContextType.PCIE_DEVICE_FUNCTION;
import static com.inspur.podm.api.business.services.context.ContextType.PORT;
import static com.inspur.podm.api.business.services.context.ContextType.PORT_METRICS;
import static com.inspur.podm.api.business.services.context.ContextType.POWER;
import static com.inspur.podm.api.business.services.context.ContextType.POWER_CONTROL;
import static com.inspur.podm.api.business.services.context.ContextType.POWER_SUPPLY;
import static com.inspur.podm.api.business.services.context.ContextType.POWER_VOLTAGE;
import static com.inspur.podm.api.business.services.context.ContextType.PROCESSOR;
import static com.inspur.podm.api.business.services.context.ContextType.PROCESSOR_METRICS;
import static com.inspur.podm.api.business.services.context.ContextType.REDUNDANCY;
import static com.inspur.podm.api.business.services.context.ContextType.SIMPLE_STORAGE;
import static com.inspur.podm.api.business.services.context.ContextType.STORAGE;
import static com.inspur.podm.api.business.services.context.ContextType.STORAGE_POOL;
import static com.inspur.podm.api.business.services.context.ContextType.STORAGE_SERVICE;
import static com.inspur.podm.api.business.services.context.ContextType.SWITCH;
import static com.inspur.podm.api.business.services.context.ContextType.THERMAL;
import static com.inspur.podm.api.business.services.context.ContextType.THERMAL_FAN;
import static com.inspur.podm.api.business.services.context.ContextType.THERMAL_TEMPERATURE;
import static com.inspur.podm.api.business.services.context.ContextType.VOLUME;
import static com.inspur.podm.api.business.services.context.ContextType.VOLUME_METRICS;
import static com.inspur.podm.api.business.services.context.ContextType.ZONE;
import static com.inspur.podm.common.intel.utils.Contracts.requiresNonNull;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;

@ApplicationScoped
public class ContextTypeToEntityMapper {
    static final Map<ContextType, Class<? extends Entity>> MAPPING;

    static {
        HashMap<ContextType, Class<? extends Entity>> map = new HashMap<>();

        map.put(MANAGER, Manager.class);
        map.put(PROCESSOR, Processor.class);
        map.put(STORAGE_SERVICE, StorageService.class);
        map.put(STORAGE_POOL, StoragePool.class);
        map.put(VOLUME, Volume.class);
        map.put(CHASSIS, Chassis.class);
        map.put(COMPUTER_SYSTEM, ComputerSystem.class);
        map.put(ENDPOINT, Endpoint.class);
        map.put(ETHERNET_SWITCH, EthernetSwitch.class);
        map.put(ETHERNET_SWITCH_METRICS, EthernetSwitchMetrics.class);
        map.put(ETHERNET_SWITCH_ACL, EthernetSwitchAcl.class);
        map.put(ETHERNET_SWITCH_ACL_RULE, EthernetSwitchAclRule.class);
        map.put(ETHERNET_SWITCH_PORT, EthernetSwitchPort.class);
        map.put(ETHERNET_SWITCH_PORT_METRICS, EthernetSwitchPortMetrics.class);
        map.put(ETHERNET_SWITCH_PORT_VLAN, EthernetSwitchPortVlan.class);
        map.put(ETHERNET_SWITCH_STATIC_MAC, EthernetSwitchStaticMac.class);
        map.put(ETHERNET_INTERFACE, EthernetInterface.class);
        map.put(MEMORY, Memory.class);
        map.put(NETWORK_PROTOCOL, NetworkProtocol.class);
        map.put(SIMPLE_STORAGE, SimpleStorage.class);
        map.put(STORAGE, Storage.class);
        map.put(COMPOSED_NODE, ComposedNode.class);
        map.put(DRIVE, Drive.class);
        map.put(DRIVE_METRICS, DriveMetrics.class);
        map.put(VOLUME_METRICS, VolumeMetrics.class);
        map.put(PCIE_DEVICE, PcieDevice.class);
        map.put(PCIE_DEVICE_FUNCTION, PcieDeviceFunction.class);
        map.put(THERMAL, Thermal.class);
        map.put(THERMAL_FAN, ThermalFan.class);
        map.put(THERMAL_TEMPERATURE, ThermalTemperature.class);
        map.put(REDUNDANCY, Redundancy.class);
        map.put(POWER, Power.class);
        map.put(POWER_VOLTAGE, PowerVoltage.class);
        map.put(POWER_CONTROL, PowerControl.class);
        map.put(POWER_SUPPLY, PowerSupply.class);
        map.put(FABRIC, Fabric.class);
        map.put(ZONE, Zone.class);
        map.put(SWITCH, Switch.class);
        map.put(PORT, Port.class);
        map.put(PORT_METRICS, PortMetrics.class);
        map.put(EVENT_SUBSCRIPTION, EventSubscription.class);
        map.put(NETWORK_INTERFACE, NetworkInterface.class);
        map.put(NETWORK_DEVICE_FUNCTION, NetworkDeviceFunction.class);
        map.put(METRIC_DEFINITION, MetricDefinition.class);
        map.put(METRIC_REPORT_DEFINITION, MetricReportDefinition.class);
        map.put(COMPUTER_SYSTEM_METRICS, ComputerSystemMetrics.class);
        map.put(PROCESSOR_METRICS, ProcessorMetrics.class);
        map.put(MEMORY_METRICS, MemoryMetrics.class);

        MAPPING = unmodifiableMap(map);
    }

    public Class<? extends Entity> getEntityClass(ContextType contextType) {
        requiresNonNull(contextType, "contextType");
        Class<? extends Entity> entityClass = MAPPING.get(contextType);
        if (entityClass == null) {
            throw new UnsupportedOperationException(format("ContextType %s has no entity class associated with it.", contextType));
        }
        return entityClass;
    }

    public ContextType tryGetContextType(Class<? extends Entity> clazz) {
        requiresNonNull(clazz, "clazz");
        return MAPPING.entrySet().stream()
            .filter(entry -> Objects.equals(entry.getValue(), clazz))
            .findFirst()
            .map(Map.Entry::getKey)
            .orElse(null);
    }
}
