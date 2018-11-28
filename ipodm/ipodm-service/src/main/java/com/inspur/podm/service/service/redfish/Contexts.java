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
import com.inspur.podm.common.persistence.entity.EthernetSwitchPort;
import com.inspur.podm.common.persistence.entity.EthernetSwitchPortVlan;
import com.inspur.podm.common.persistence.entity.EthernetSwitchStaticMac;
import com.inspur.podm.common.persistence.entity.EventSubscription;
import com.inspur.podm.common.persistence.entity.Fabric;
import com.inspur.podm.common.persistence.entity.Manager;
import com.inspur.podm.common.persistence.entity.Memory;
import com.inspur.podm.common.persistence.entity.MemoryMetrics;
import com.inspur.podm.common.persistence.entity.MetricDefinition;
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
import com.inspur.podm.common.persistence.entity.DiscoverableEntity;
import com.inspur.podm.common.persistence.base.Entity;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.common.intel.types.Id;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.inspur.podm.api.business.services.context.Context.contextOf;
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
import static com.inspur.podm.api.business.services.context.ContextType.ETHERNET_SWITCH_PORT;
import static com.inspur.podm.api.business.services.context.ContextType.ETHERNET_SWITCH_PORT_VLAN;
import static com.inspur.podm.api.business.services.context.ContextType.ETHERNET_SWITCH_STATIC_MAC;
import static com.inspur.podm.api.business.services.context.ContextType.EVENT_SERVICE;
import static com.inspur.podm.api.business.services.context.ContextType.EVENT_SUBSCRIPTION;
import static com.inspur.podm.api.business.services.context.ContextType.FABRIC;
import static com.inspur.podm.api.business.services.context.ContextType.MANAGER;
import static com.inspur.podm.api.business.services.context.ContextType.MEMORY;
import static com.inspur.podm.api.business.services.context.ContextType.MEMORY_METRICS;
import static com.inspur.podm.api.business.services.context.ContextType.METRIC_DEFINITION;
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
import static com.inspur.podm.api.business.services.context.ContextType.TELEMETRY_SERVICE;
import static com.inspur.podm.api.business.services.context.ContextType.THERMAL;
import static com.inspur.podm.api.business.services.context.ContextType.THERMAL_FAN;
import static com.inspur.podm.api.business.services.context.ContextType.THERMAL_TEMPERATURE;
import static com.inspur.podm.api.business.services.context.ContextType.VOLUME;
import static com.inspur.podm.api.business.services.context.ContextType.VOLUME_METRICS;
import static com.inspur.podm.api.business.services.context.ContextType.ZONE;
import static com.inspur.podm.common.intel.utils.Unproxier.unproxy;
import static com.inspur.podm.common.intel.types.Id.id;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:MethodCount"})
public final class Contexts {

    private static final Map<Class<?>, Method> CLASS_TO_CONTEXT_METHOD_MAPPING = stream(Contexts.class.getDeclaredMethods())
        .filter(method -> method.getReturnType().equals(Context.class))
        .filter(method -> method.getParameterTypes().length == 1)
        .collect(toMap(method -> method.getParameterTypes()[0], m -> m))
        .entrySet().stream()
        .filter(entry -> Entity.class.isAssignableFrom(entry.getKey()))
        .filter(entry -> !Entity.class.equals(entry.getKey()))
        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

    private Contexts() {
    }

    public static Context toContext(Entity entity) {
        if (entity == null) {
            return null;
        }
        try {
            return (Context) ofNullable(CLASS_TO_CONTEXT_METHOD_MAPPING.get(unproxy(entity.getClass())))
                .orElseThrow(() -> new UnsupportedOperationException("Contexts.toContext(" + entity.getClass() + ") method is not implemented"))
                .invoke(null, entity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<Context> tryConvertToContext(Entity entity) {
        if (entity == null) {
            return empty();
        }

        Optional<Method> method = ofNullable(CLASS_TO_CONTEXT_METHOD_MAPPING.get(unproxy(entity.getClass())));
        try {
            if (method.isPresent()) {
                return of((Context) method.get().invoke(null, entity));
            }
            return empty();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T extends DiscoverableEntity, R extends DiscoverableEntity> Context getParentContext(T entity, Function<T, R> function) {
        R parent = function.apply(entity);
        if (parent == null) {
            throw new RuntimeException(
                format("Parent context for %s#%s could not be created. Parent does not exist.", entity.getClass().getSimpleName(), entity.getTheId())
            );
        }
        return toContext(parent);
    }

    private static Context toContext(Port port) {
        return getParentContext(port, Port::getSwitch).child(port.getTheId(), PORT);
    }

    private static Context toContext(Switch fabricSwitch) {
        return getParentContext(fabricSwitch, Switch::getFabric).child(fabricSwitch.getTheId(), SWITCH);
    }

    private static Context toContext(Zone zone) {
        return getParentContext(zone, Zone::getFabric).child(zone.getTheId(), ZONE);
    }

    private static Context toContext(Chassis chassis) {
        return contextOf(chassis.getTheId(), CHASSIS);
    }

    private static Context toContext(ComputerSystem computerSystem) {
        return contextOf(computerSystem.getTheId(), COMPUTER_SYSTEM);
    }

    private static Context toContext(PortMetrics portMetrics) {
        return getParentContext(portMetrics, PortMetrics::getPort).child(id(""), PORT_METRICS);
    }

    private static Context toContext(Thermal thermal) {
        return getParentContext(thermal, Thermal::getChassis).child(id(""), THERMAL);
    }

    private static Context toContext(ThermalFan thermalFan) {
        return getParentContext(thermalFan, ThermalFan::getThermal).child(id(getLastSegment(thermalFan.getTheId())), THERMAL_FAN);
    }

    private static Context toContext(ThermalTemperature thermalTemperature) {
        return getParentContext(thermalTemperature, ThermalTemperature::getThermal)
            .child(id(getLastSegment(thermalTemperature.getTheId())), THERMAL_TEMPERATURE);
    }

    private static Context toContext(Redundancy redundancy) {
        return getParentContext(redundancy, Redundancy::getRedundancyOwner).child(id(getLastSegment(redundancy.getTheId())), REDUNDANCY);
    }

    private static Context toContext(Power power) {
        return getParentContext(power, Power::getChassis).child(id(""), POWER);
    }

    private static Context toContext(PowerVoltage powerVoltage) {
        return getParentContext(powerVoltage, PowerVoltage::getPower).child(id(getLastSegment(powerVoltage.getTheId())), POWER_VOLTAGE);
    }

    private static Context toContext(PowerSupply powerSupply) {
        return getParentContext(powerSupply, PowerSupply::getPower).child(id(getLastSegment(powerSupply.getTheId())), POWER_SUPPLY);
    }

    private static Context toContext(PowerControl powerControl) {
        return getParentContext(powerControl, PowerControl::getPower).child(id(getLastSegment(powerControl.getTheId())), POWER_CONTROL);
    }

    private static Context toContext(Memory memory) {
        return getParentContext(memory, Memory::getComputerSystem).child(memory.getTheId(), MEMORY);
    }

    private static Context toContext(MemoryMetrics memoryMetrics) {
        return getParentContext(memoryMetrics, MemoryMetrics::getMemory).child(id(""), MEMORY_METRICS);
    }

    private static Context toContext(Fabric fabric) {
        return contextOf(fabric.getTheId(), FABRIC);
    }

    private static Context toContext(Processor processor) {
        return getParentContext(processor, Processor::getComputerSystem).child(processor.getTheId(), PROCESSOR);
    }

    private static Context toContext(EthernetInterface ethernetInterface) {
        if (ethernetInterface.getComputerSystem() != null) {
            return getParentContext(ethernetInterface, EthernetInterface::getComputerSystem).child(ethernetInterface.getTheId(), ETHERNET_INTERFACE);
        }
        Manager parent = ethernetInterface.getManagers().stream()
            .findFirst()
            .orElseThrow(() -> new UnsupportedOperationException(
                format("Contexts.toContext(%s) could not be performed. Parent Manager could not be found.", ethernetInterface.getClass())));
        return toContext(parent).child(ethernetInterface.getTheId(), ETHERNET_INTERFACE);
    }

    private static Context toContext(EthernetSwitchPort port) {
        return getParentContext(port, EthernetSwitchPort::getEthernetSwitch).child(port.getTheId(), ETHERNET_SWITCH_PORT);
    }

    private static Context toContext(EthernetSwitchPortVlan vlan) {
        if (vlan.getEthernetSwitchPort() != null) {
            return getParentContext(vlan, EthernetSwitchPortVlan::getEthernetSwitchPort).child(vlan.getTheId(), ETHERNET_SWITCH_PORT_VLAN);
        }
        return getParentContext(vlan, EthernetSwitchPortVlan::getEthernetInterface).child(vlan.getTheId(), ETHERNET_SWITCH_PORT_VLAN);
    }

    private static Context toContext(EthernetSwitchStaticMac ethernetSwitchStaticMac) {
        return getParentContext(ethernetSwitchStaticMac, EthernetSwitchStaticMac::getEthernetSwitchPort).child(ethernetSwitchStaticMac.getTheId(),
            ETHERNET_SWITCH_STATIC_MAC);
    }

    private static Context toContext(EthernetSwitchAcl acl) {
        return getParentContext(acl, EthernetSwitchAcl::getEthernetSwitch).child(acl.getTheId(), ETHERNET_SWITCH_ACL);
    }

    private static Context toContext(EthernetSwitchAclRule aclRule) {
        return getParentContext(aclRule, EthernetSwitchAclRule::getEthernetSwitchAcl).child(aclRule.getTheId(), ETHERNET_SWITCH_ACL_RULE);
    }

    private static Context toContext(StorageService storageService) {
        return contextOf(storageService.getTheId(), STORAGE_SERVICE);
    }

    private static Context toContext(SimpleStorage simpleStorage) {
        return getParentContext(simpleStorage, SimpleStorage::getComputerSystem).child(simpleStorage.getTheId(), SIMPLE_STORAGE);
    }

    private static Context toContext(StoragePool storagePool) {
        return getParentContext(storagePool, StoragePool::getStorageService).child(storagePool.getTheId(), STORAGE_POOL);
    }

    private static Context toContext(Volume volume) {
        return getParentContext(volume, Volume::getStorageService).child(volume.getTheId(), VOLUME);
    }

    private static Context toContext(NetworkProtocol networkProtocol) {
        return contextOf(networkProtocol.getTheId(), NETWORK_PROTOCOL);
    }

    private static Context toContext(Manager manager) {
        return contextOf(manager.getTheId(), MANAGER);
    }

    private static Context toContext(Endpoint endpoint) {
        return getParentContext(endpoint, Endpoint::getFabric).child(endpoint.getTheId(), ENDPOINT);
    }

    private static Context toContext(Drive drive) {
        return getParentContext(drive, Drive::getChassis).child(drive.getTheId(), DRIVE);
    }

    private static Context toContext(DriveMetrics driveMetrics) {
        return getParentContext(driveMetrics, DriveMetrics::getDrive).child(id(""), DRIVE_METRICS);
    }

    private static Context toContext(VolumeMetrics volumeMetrics) {
        return getParentContext(volumeMetrics, VolumeMetrics::getVolume).child(id(""), VOLUME_METRICS);
    }

    private static Context toContext(Storage storage) {
        return getParentContext(storage, Storage::getComputerSystem).child(storage.getTheId(), STORAGE);
    }

    private static Context toContext(EthernetSwitch ethernetSwitch) {
        return contextOf(ethernetSwitch.getTheId(), ETHERNET_SWITCH);
    }

    private static Context toContext(PcieDevice pcieDevice) {
        Chassis chassis = pcieDevice.getChassis().stream().findFirst()
            .orElseThrow(() -> new UnsupportedOperationException(format("Contexts.toContext(%s) could not be performed. "
                + "Parent chassis could not be found.", pcieDevice.getClass())));
        return toContext(chassis).child(pcieDevice.getTheId(), PCIE_DEVICE);
    }

    private static Context toContext(PcieDeviceFunction pcieDeviceFunction) {
        return getParentContext(pcieDeviceFunction, PcieDeviceFunction::getPcieDevice).child(pcieDeviceFunction.getTheId(), PCIE_DEVICE_FUNCTION);
    }

    private static Context toContext(ComposedNode composedNode) {
        return contextOf(composedNode.getTheId(), COMPOSED_NODE);
    }

    private static Context toContext(EventSubscription eventSubscription) {
        return contextOf(id(""), EVENT_SERVICE).child(eventSubscription.getTheId(), EVENT_SUBSCRIPTION);
    }

    private static Context toContext(MetricDefinition metricDefinition) {
        return contextOf(id(""), TELEMETRY_SERVICE).child(metricDefinition.getTheId(), METRIC_DEFINITION);
    }

    private static Context toContext(ComputerSystemMetrics computerSystemMetrics) {
        return getParentContext(computerSystemMetrics, ComputerSystemMetrics::getComputerSystem).child(id(""), COMPUTER_SYSTEM_METRICS);
    }

    private static Context toContext(ProcessorMetrics processorMetrics) {
        return getParentContext(processorMetrics, ProcessorMetrics::getProcessor).child(id(""), PROCESSOR_METRICS);
    }

    private static Context toContext(NetworkInterface networkInterface) {
        return getParentContext(networkInterface, NetworkInterface::getComputerSystem).child(networkInterface.getTheId(), NETWORK_INTERFACE);
    }

    private static Context toContext(NetworkDeviceFunction networkDeviceFunction) {
        return getParentContext(networkDeviceFunction, NetworkDeviceFunction::getNetworkInterface).child(networkDeviceFunction.getTheId(),
            NETWORK_DEVICE_FUNCTION);
    }

    private static String getLastSegment(Id id) {
        return id.getValue().substring(id.getValue().lastIndexOf('-') + 1);
    }
}
