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


import java.math.BigDecimal;
import java.util.Objects;

import com.inspur.podm.common.intel.types.Id;
import com.inspur.podm.common.persistence.BaseEntity;
import com.inspur.podm.common.persistence.entity.embeddables.PowerLimit;
import com.inspur.podm.common.persistence.entity.embeddables.PowerMetrics;

//@javax.persistence.Entity
//@Table(name = "power_control", indexes = @Index(name = "idx_power_control_entity_id", columnList = "entity_id", unique = true))
//@SuppressWarnings({"checkstyle:MethodCount"})
//@Eventable
public class PowerControl extends DiscoverableEntity {
/** @Fields serialVersionUID: TODO 功能描述  */
	private static final long serialVersionUID = -2441175705148478965L;

	//    @Column(name = "entity_id", columnDefinition = ENTITY_ID_STRING_COLUMN_DEFINITION)
    private Id entityId;

//    @Column(name = "member_id")
    private String memberId;

//    @Column(name = "power_consumed_watts")
    private BigDecimal powerConsumedWatts;

//    @Column(name = "power_requested_watts")
    private BigDecimal powerRequestedWatts;

//    @Column(name = "power_available_watts")
    private BigDecimal powerAvailableWatts;

//    @Column(name = "power_capacity_watts")
    private BigDecimal powerCapacityWatts;

//    @Column(name = "power_allocated_watts")
    private BigDecimal powerAllocatedWatts;

//    @Column(name = "oem")
    private String oem;

//    @Embedded
    private PowerMetrics powerMetrics;

//    @Embedded
    private PowerLimit powerLimit;

//    @ManyToOne(fetch = LAZY, cascade = {MERGE, PERSIST})
//    @JoinColumn(name = "power_id")
    private Power power;

    @Override
    public Id getId() {
        return entityId;
    }

    @Override
    public void setId(Id id) {
        entityId = id;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public BigDecimal getPowerConsumedWatts() {
        return powerConsumedWatts;
    }

    public void setPowerConsumedWatts(BigDecimal powerConsumedWatts) {
        this.powerConsumedWatts = powerConsumedWatts;
    }

    public BigDecimal getPowerRequestedWatts() {
        return powerRequestedWatts;
    }

    public void setPowerRequestedWatts(BigDecimal powerRequestedWatts) {
        this.powerRequestedWatts = powerRequestedWatts;
    }

    public BigDecimal getPowerAvailableWatts() {
        return powerAvailableWatts;
    }

    public void setPowerAvailableWatts(BigDecimal powerAvailableWatts) {
        this.powerAvailableWatts = powerAvailableWatts;
    }

    public BigDecimal getPowerCapacityWatts() {
        return powerCapacityWatts;
    }

    public void setPowerCapacityWatts(BigDecimal powerCapacityWatts) {
        this.powerCapacityWatts = powerCapacityWatts;
    }

    public BigDecimal getPowerAllocatedWatts() {
        return powerAllocatedWatts;
    }

    public void setPowerAllocatedWatts(BigDecimal powerAllocatedWatts) {
        this.powerAllocatedWatts = powerAllocatedWatts;
    }

    public String getOem() {
        return oem;
    }

    public void setOem(String oem) {
        this.oem = oem;
    }

    public PowerMetrics getPowerMetrics() {
        return powerMetrics;
    }

    public void setPowerMetrics(PowerMetrics powerMetrics) {
        this.powerMetrics = powerMetrics;
    }

    public PowerLimit getPowerLimit() {
        return powerLimit;
    }

    public void setPowerLimit(PowerLimit powerLimit) {
        this.powerLimit = powerLimit;
    }

//    @EventOriginProvider
    public Power getPower() {
        return power;
    }

    public void setPower(Power power) {
        if (!Objects.equals(this.power, power)) {
            unlinkPower(this.power);
            this.power = power;
            if (power != null && !power.getPowerControls().contains(this)) {
                power.addPowerControl(this);
            }
        }
    }

    public void unlinkPower(Power power) {
        if (Objects.equals(this.power, power)) {
            this.power = null;
            if (power != null) {
                power.unlinkPowerControl(this);
            }
        }
    }

    @Override
    public void preRemove() {
        unlinkPower(power);
    }

    @Override
    public boolean containedBy(BaseEntity possibleParent) {
        return isContainedBy(possibleParent, power);
    }
}
