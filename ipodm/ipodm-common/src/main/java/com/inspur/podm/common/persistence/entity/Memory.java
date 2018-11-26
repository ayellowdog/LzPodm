/*
 * Copyright (c) 2016-2018 Intel Corporation
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

package com.inspur.podm.common.persistence.entity;


import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import com.inspur.podm.common.intel.types.BaseModuleType;
import com.inspur.podm.common.intel.types.ErrorCorrection;
import com.inspur.podm.common.intel.types.Id;
import com.inspur.podm.common.intel.types.MemoryDeviceType;
import com.inspur.podm.common.intel.types.MemoryMedia;
import com.inspur.podm.common.intel.types.MemoryType;
import com.inspur.podm.common.intel.types.OperatingMemoryMode;
import com.inspur.podm.common.persistence.base.Entity;
import com.inspur.podm.common.persistence.base.MemoryModule;
import com.inspur.podm.common.persistence.base.MultiSourceResource;
import com.inspur.podm.common.persistence.entity.embeddables.MemoryLocation;
import com.inspur.podm.common.persistence.entity.embeddables.Region;
@javax.persistence.Entity
@Table(name = "memory", indexes = @Index(name = "idx_memory_entity_id", columnList = "entity_id", unique = true))
@NamedQueries({
    @NamedQuery(name = Memory.GET_MEMORY_MULTI_SOURCE,
        query = "SELECT memory "
            + "FROM Memory memory "
            + "WHERE memory.multiSourceDiscriminator = :multiSourceDiscriminator "
            + "AND memory.isComplementary = :isComplementary"
    )
})
//@Eventable
//@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:MethodCount"})
public class Memory extends DiscoverableEntity implements MemoryModule, MultiSourceResource {

	public static final String GET_MEMORY_MULTI_SOURCE = "GET_MEMORY_MULTI_SOURCE";

    @Column(name = "entity_id", columnDefinition = ENTITY_ID_STRING_COLUMN_DEFINITION)
    private Id entityId;

    @Column(name = "memory_type")
    @Enumerated(STRING)
    private MemoryType memoryType;

    @Column(name = "memory_device_type")
    @Enumerated(STRING)
    private MemoryDeviceType memoryDeviceType;

    @Column(name = "base_module_type")
    @Enumerated(STRING)
    private BaseModuleType baseModuleType;

    @Column(name = "capacity_mib")
    private Integer capacityMib;

    @Column(name = "data_width_bits")
    private Integer dataWidthBits;

    @Column(name = "bus_width_bits")
    private Integer busWidthBits;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "part_number")
    private String partNumber;

    @Column(name = "firmware_revision")
    private String firmwareRevision;

    @Column(name = "firmware_api_version")
    private String firmwareApiVersion;

    @Column(name = "vendor_id")
    private String vendorId;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "rank_count")
    private Integer rankCount;

    @Column(name = "device_locator")
    private String deviceLocator;

    @Column(name = "error_correction")
    @Enumerated(STRING)
    private ErrorCorrection errorCorrection;

    @Column(name = "operating_speed_mhz")
    private Integer operatingSpeedMhz;

    @Column(name = "voltage_volt")
    private BigDecimal voltageVolt;

    @Column(name = "multi_source_discriminator")
    private String multiSourceDiscriminator;

    @Embedded
    private MemoryLocation memoryLocation;

    @ElementCollection
    @Enumerated(STRING)
    @CollectionTable(name = "memory_memory_media", joinColumns = @JoinColumn(name = "memory_id"))
    @Column(name = "memory_media")
    @OrderColumn(name = "memory_media_order")
    private List<MemoryMedia> memoryMedia = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "memory_allowed_speed_mhz", joinColumns = @JoinColumn(name = "memory_id"))
    @Column(name = "allowed_speed_mhz")
    @OrderColumn(name = "allowed_speed_mhz_order")
    private List<Integer> allowedSpeedsMhz = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "memory_function_class", joinColumns = @JoinColumn(name = "memory_id"))
    @Column(name = "function_class")
    @OrderColumn(name = "function_class_order")
    private List<String> functionClasses = new ArrayList<>();

    @ElementCollection
    @Enumerated(STRING)
    @CollectionTable(name = "memory_operating_memory_mode", joinColumns = @JoinColumn(name = "memory_id"))
    @Column(name = "operating_memory_mode")
    @OrderColumn(name = "operating_memory_mode_order")
    private List<OperatingMemoryMode> operatingMemoryModes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "memory_region", joinColumns = @JoinColumn(name = "memory_id"))
    @OrderColumn(name = "memory_region_order")
    private List<Region> regions = new ArrayList<>();

    @ManyToOne(fetch = LAZY, cascade = {MERGE, PERSIST})
    @JoinColumn(name = "computer_system_id")
    private ComputerSystem computerSystem;

    @OneToOne(mappedBy = "memory", fetch = LAZY, cascade = {MERGE, PERSIST})
    private MemoryMetrics memoryMetrics;

    @Override
    public Id getId() {
        return entityId;
    }

    @Override
    public void setId(Id id) {
        entityId = id;
    }

    public MemoryType getMemoryType() {
        return memoryType;
    }

    public void setMemoryType(MemoryType memoryType) {
        this.memoryType = memoryType;
    }

    @Override
    public MemoryDeviceType getMemoryDeviceType() {
        return memoryDeviceType;
    }

    public void setMemoryDeviceType(MemoryDeviceType memoryDeviceType) {
        this.memoryDeviceType = memoryDeviceType;
    }

    public BaseModuleType getBaseModuleType() {
        return baseModuleType;
    }

    public void setBaseModuleType(BaseModuleType baseModuleType) {
        this.baseModuleType = baseModuleType;
    }

    @Override
    public Integer getCapacityMib() {
        return capacityMib;
    }

    public void setCapacityMib(Integer capacityMib) {
        this.capacityMib = capacityMib;
    }

    @Override
    public Integer getDataWidthBits() {
        return dataWidthBits;
    }

    public void setDataWidthBits(Integer dataWidthBits) {
        this.dataWidthBits = dataWidthBits;
    }

    public Integer getBusWidthBits() {
        return busWidthBits;
    }

    public void setBusWidthBits(Integer busWidthBits) {
        this.busWidthBits = busWidthBits;
    }

    @Override
    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getFirmwareRevision() {
        return firmwareRevision;
    }

    public void setFirmwareRevision(String firmwareRevision) {
        this.firmwareRevision = firmwareRevision;
    }

    public String getFirmwareApiVersion() {
        return firmwareApiVersion;
    }

    public void setFirmwareApiVersion(String firmwareApiVersion) {
        this.firmwareApiVersion = firmwareApiVersion;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getRankCount() {
        return rankCount;
    }

    public void setRankCount(Integer rankCount) {
        this.rankCount = rankCount;
    }

    public String getDeviceLocator() {
        return deviceLocator;
    }

    public void setDeviceLocator(String deviceLocator) {
        this.deviceLocator = deviceLocator;
    }

    public ErrorCorrection getErrorCorrection() {
        return errorCorrection;
    }

    public void setErrorCorrection(ErrorCorrection errorCorrection) {
        this.errorCorrection = errorCorrection;
    }

    @Override
    public Integer getOperatingSpeedMhz() {
        return operatingSpeedMhz;
    }

    public void setOperatingSpeedMhz(Integer operatingSpeedMhz) {
        this.operatingSpeedMhz = operatingSpeedMhz;
    }

    public BigDecimal getVoltageVolt() {
        return voltageVolt;
    }

    public void setVoltageVolt(BigDecimal voltageVolt) {
        this.voltageVolt = voltageVolt;
    }

    @Override
    public String getMultiSourceDiscriminator() {
        return multiSourceDiscriminator;
    }

    @Override
    public void setMultiSourceDiscriminator(String multiSourceDiscriminator) {
        this.multiSourceDiscriminator = multiSourceDiscriminator;
    }

    public MemoryLocation getMemoryLocation() {
        return memoryLocation;
    }

    public void setMemoryLocation(MemoryLocation memoryLocation) {
        this.memoryLocation = memoryLocation;
    }

    public List<MemoryMedia> getMemoryMedia() {
        return memoryMedia;
    }

    public void addMemoryMedia(MemoryMedia memoryMedia) {
        this.memoryMedia.add(memoryMedia);
    }

    public List<Integer> getAllowedSpeedsMhz() {
        return allowedSpeedsMhz;
    }

    public void addAllowedSpeedMhz(Integer allowedSpeed) {
        this.allowedSpeedsMhz.add(allowedSpeed);
    }

    public List<String> getFunctionClasses() {
        return functionClasses;
    }

    public void addFunctionClass(String functionClass) {
        this.functionClasses.add(functionClass);
    }

    public List<OperatingMemoryMode> getOperatingMemoryModes() {
        return operatingMemoryModes;
    }

    public void addOperatingMemoryMode(OperatingMemoryMode operatingMemoryMode) {
        this.operatingMemoryModes.add(operatingMemoryMode);
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void addRegion(Region region) {
        regions.add(region);
    }

    public ComputerSystem getComputerSystem() {
        return computerSystem;
    }

    public void setComputerSystem(ComputerSystem computerSystem) {
        if (!Objects.equals(this.computerSystem, computerSystem)) {
            unlinkComputerSystem(this.computerSystem);
            this.computerSystem = computerSystem;
            if (computerSystem != null && !computerSystem.getMemoryModules().contains(this)) {
                computerSystem.addMemoryModule(this);
            }
        }
    }

    public void unlinkComputerSystem(ComputerSystem computerSystem) {
        if (Objects.equals(this.computerSystem, computerSystem)) {
            this.computerSystem = null;
            if (computerSystem != null) {
                computerSystem.unlinkMemoryModule(this);
            }
        }
    }

    public MemoryMetrics getMemoryMetrics() {
        return memoryMetrics;
    }

    public void setMemoryMetrics(MemoryMetrics memoryMetrics) {
        if (!Objects.equals(this.memoryMetrics, memoryMetrics)) {
            unlinkMemoryMetrics(this.memoryMetrics);
            this.memoryMetrics = memoryMetrics;
            if (memoryMetrics != null && !this.equals(memoryMetrics.getMemory())) {
                memoryMetrics.setMemory(this);
            }
        }
    }

    public void unlinkMemoryMetrics(MemoryMetrics memoryMetrics) {
        if (Objects.equals(this.memoryMetrics, memoryMetrics)) {
            this.memoryMetrics = null;
            if (memoryMetrics != null) {
                memoryMetrics.unlinkMemory(this);
            }
        }
    }

    @Override
    public void preRemove() {
        unlinkComputerSystem(computerSystem);
        unlinkMemoryMetrics(memoryMetrics);
    }

    @Override
    public boolean containedBy(Entity possibleParent) {
        return isContainedBy(possibleParent, computerSystem);
    }
}
