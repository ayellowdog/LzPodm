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

package com.inspur.podm.service.dao;



import static com.inspur.podm.common.utils.Contracts.requiresNonNull;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.inspur.podm.common.intel.types.ChassisType;
import com.inspur.podm.common.intel.types.Id;
import com.inspur.podm.common.intel.types.IndicatorLed;
import com.inspur.podm.common.intel.types.PowerState;
import com.inspur.podm.common.persistence.BaseEntity;
import com.inspur.podm.common.persistence.entity.DiscoverableEntity;
import com.inspur.podm.common.persistence.entity.embeddables.RackChassisAttributes;

@javax.persistence.Entity
@Table(name = "chassis", indexes = @Index(name = "idx_chassis_entity_id", columnList = "entity_id", unique = true))
//@Eventable
@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:MethodCount"})
public class MyChassis extends DiscoverableEntity {
    /** @Fields serialVersionUID: TODO 功能描述  */
	private static final long serialVersionUID = -6454277548966473550L;
	public static final String GET_CHASSIS_BY_TYPE = "GET_CHASSIS_BY_TYPE";
    public static final String GET_CHASSIS_IDS_FROM_PRIMARY_DATA_SOURCE = "GET_CHASSIS_IDS_FROM_PRIMARY_DATA_SOURCE";
    public static final String GET_CHASSIS_MULTI_SOURCE = "GET_CHASSIS_MULTI_SOURCE";
    public static final String GET_CHASSIS_BY_TYPE_AND_SERVICE = "GET_CHASSIS_BY_TYPE_AND_SERVICE";
    public static final String GET_CHASSIS_BY_TYPE_AND_LOCATION = "GET_CHASSIS_BY_TYPE_AND_LOCATION";

    @Column(name = "entity_id", columnDefinition = ENTITY_ID_STRING_COLUMN_DEFINITION)
    private Id entityId;

    @Column(name = "chassis_type")
    @Enumerated(STRING)
    private ChassisType chassisType;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "model")
    private String model;

    @Column(name = "sku")
    private String sku;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "part_number")
    private String partNumber;

    @Column(name = "asset_tag")
    private String assetTag;

    @Column(name = "indicator_led")
    @Enumerated(STRING)
    private IndicatorLed indicatorLed;

    @Column(name = "location_id")
    private String locationId;

    @Column(name = "location_parent_id")
    private String locationParentId;

    @Column(name = "power_state")
    @Enumerated(STRING)
    private PowerState powerState;

    @Embedded
    private RackChassisAttributes rackChassisAttributes;

   

    @Override
    public Id getId() {
        return entityId;
    }

    @Override
    public void setId(Id id) {
        entityId = id;
    }

    public ChassisType getChassisType() {
        return chassisType;
    }

    public void setChassisType(ChassisType chassisType) {
        this.chassisType = chassisType;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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

    public String getAssetTag() {
        return assetTag;
    }

    public void setAssetTag(String assetTag) {
        this.assetTag = assetTag;
    }

    public IndicatorLed getIndicatorLed() {
        return indicatorLed;
    }

    public void setIndicatorLed(IndicatorLed indicatorLed) {
        this.indicatorLed = indicatorLed;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationParentId() {
        return locationParentId;
    }

    public void setLocationParentId(String locationParentId) {
        this.locationParentId = locationParentId;
    }

    public PowerState getPowerState() {
        return powerState;
    }

    public void setPowerState(PowerState powerState) {
        this.powerState = powerState;
    }

    public RackChassisAttributes getRackChassisAttributes() {
        return rackChassisAttributes;
    }

    public void setRackChassisAttributes(RackChassisAttributes rackChassisAttributes) {
        this.rackChassisAttributes = rackChassisAttributes;
    }










 




    @Override
    public void preRemove() {

    }

    @Override
    public boolean containedBy(BaseEntity possibleParent) {
        return false;
    }

}
