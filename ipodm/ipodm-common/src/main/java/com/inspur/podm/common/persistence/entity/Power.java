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


import static com.inspur.podm.common.utils.Contracts.requiresNonNull;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.inspur.podm.common.intel.types.Id;
import com.inspur.podm.common.persistence.BaseEntity;

//@javax.persistence.Entity
//@Table(name = "power", indexes = @Index(name = "idx_power_entity_id", columnList = "entity_id", unique = true))
//@SuppressWarnings({"checkstyle:MethodCount"})
//@Eventable
public class Power extends DiscoverableEntity {
/** @Fields serialVersionUID: TODO 功能描述  */
	private static final long serialVersionUID = 5591475026677656710L;

	//    @Column(name = "entity_id", columnDefinition = ENTITY_ID_STRING_COLUMN_DEFINITION)
    private Id entityId;

//    @Column(name = "input_ac_power_watts")
    private BigDecimal inputAcPowerWatts;

//    @OneToMany(mappedBy = "power", fetch = LAZY, cascade = {MERGE, PERSIST})
    private Set<PowerControl> powerControls = new HashSet<>();

//    @OneToMany(mappedBy = "power", fetch = LAZY, cascade = {MERGE, PERSIST})
    private Set<PowerVoltage> voltages = new HashSet<>();

//    @OneToMany(mappedBy = "power", fetch = LAZY, cascade = {MERGE, PERSIST})
    private Set<PowerSupply> powerSupplies = new HashSet<>();

//    @ManyToMany(mappedBy = "poweredBy", fetch = LAZY, cascade = {MERGE, PERSIST})
    private Set<Chassis> poweredChassis = new HashSet<>();

//    @OneToOne(fetch = LAZY, cascade = {MERGE, PERSIST})
//    @JoinColumn(name = "chassis_id")
    private Chassis chassis;

    @Override
    public Id getId() {
        return entityId;
    }

    @Override
    public void setId(Id id) {
        this.entityId = id;
    }

    public BigDecimal getInputAcPowerWatts() {
        return inputAcPowerWatts;
    }

    public void setInputAcPowerWatts(BigDecimal inputAcPowerWatts) {
        this.inputAcPowerWatts = inputAcPowerWatts;
    }

    public Set<PowerControl> getPowerControls() {
        return powerControls;
    }

    public void addPowerControl(PowerControl powerControl) {
        requiresNonNull(powerControl, "powerControl");

        powerControls.add(powerControl);
        if (!this.equals(powerControl.getPower())) {
            powerControl.setPower(this);
        }
    }

    public void unlinkPowerControl(PowerControl powerControl) {
        if (powerControls.contains(powerControl)) {
            powerControls.remove(powerControl);
            if (powerControl != null) {
                powerControl.unlinkPower(this);
            }
        }
    }

    public Set<PowerVoltage> getVoltages() {
        return voltages;
    }

    public void addVoltage(PowerVoltage voltage) {
        requiresNonNull(voltage, "voltage");

        voltages.add(voltage);
        if (!this.equals(voltage.getPower())) {
            voltage.setPower(this);
        }
    }

    public void unlinkVoltage(PowerVoltage voltage) {
        if (voltages.contains(voltage)) {
            voltages.remove(voltage);
            if (voltage != null) {
                voltage.unlinkPower(this);
            }
        }
    }

    public Set<PowerSupply> getPowerSupplies() {
        return powerSupplies;
    }

    public void addPowerSupply(PowerSupply powerSupply) {
        requiresNonNull(powerSupply, "powerSupply");

        powerSupplies.add(powerSupply);
        if (!this.equals(powerSupply.getPower())) {
            powerSupply.setPower(this);
        }
    }

    public void unlinkPowerSupply(PowerSupply powerSupply) {
        if (powerSupplies.contains(powerSupply)) {
            powerSupplies.remove(powerSupply);
            if (powerSupply != null) {
                powerSupply.unlinkPower(this);
            }
        }
    }

    public Set<Chassis> getPoweredChassis() {
        return poweredChassis;
    }

    public void addPoweredChassis(Chassis chassis) {
        requiresNonNull(chassis, "chassis");

        poweredChassis.add(chassis);
        if (!chassis.getPoweredBy().contains(this)) {
            chassis.addPoweredBy(this);
        }
    }

    public void unlinkPoweredChassis(Chassis chassis) {
        if (poweredChassis.contains(chassis)) {
            poweredChassis.remove(chassis);
            if (chassis != null) {
                chassis.unlinkPoweredBy(this);
            }
        }
    }

    public Chassis getChassis() {
        return chassis;
    }

    public void setChassis(Chassis chassis) {
        if (!Objects.equals(this.chassis, chassis)) {
            unlinkChassis(this.chassis);
            this.chassis = chassis;
            if (chassis != null && !this.equals(chassis.getPower())) {
                chassis.setPower(this);
            }
        }
    }

    public void unlinkChassis(Chassis chassis) {
        if (Objects.equals(this.chassis, chassis)) {
            this.chassis = null;
            if (chassis != null) {
                chassis.unlinkPower(this);
            }
        }
    }

    @Override
    public void preRemove() {
        unlinkCollection(powerControls, this::unlinkPowerControl);
        unlinkCollection(voltages, this::unlinkVoltage);
        unlinkCollection(powerSupplies, this::unlinkPowerSupply);
        unlinkCollection(poweredChassis, this::unlinkPoweredChassis);
        unlinkChassis(chassis);
    }

    @Override
    public boolean containedBy(BaseEntity possibleParent) {
        return isContainedBy(possibleParent, chassis);
    }
}
