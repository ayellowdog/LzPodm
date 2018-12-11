/*
 * Copyright (c) 2017-2018 Intel Corporation
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

package com.intel.podm.business.entities.redfish;


import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.LAZY;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.intel.podm.business.entities.redfish.base.Entity;
import com.intel.podm.common.types.Id;
@javax.persistence.Entity
@Table(name = "ethernet_switch_metrics", indexes = @Index(name = "idx_ethernet_switch_metrics_entity_id", columnList = "entity_id", unique = true))
public class EthernetSwitchMetrics extends DiscoverableEntity {

    @Column(name = "entity_id", columnDefinition = ENTITY_ID_STRING_COLUMN_DEFINITION)
    private Id entityId;

    @OneToOne(fetch = LAZY, cascade = {MERGE, PERSIST})
    @JoinColumn(name = "ethernet_switch_id")
    private EthernetSwitch ethernetSwitch;

    @Column(name = "health")
    private String health;

    @Override
    public void preRemove() {
        unlinkEthernetSwitch(ethernetSwitch);
    }

    @Override
    public boolean containedBy(Entity possibleParent) {
        return false;
    }

    @Override
    public Id getTheId() {
        return entityId;
    }

    @Override
    public void setTheId(Id id) {
        entityId = id;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public EthernetSwitch getEthernetSwitch() {
        return ethernetSwitch;
    }

    public void setEthernetSwitch(EthernetSwitch ethernetSwitch) {
        if (!Objects.equals(this.ethernetSwitch, ethernetSwitch)) {
            unlinkEthernetSwitch(this.ethernetSwitch);
            this.ethernetSwitch = ethernetSwitch;
            if (ethernetSwitch != null && !this.equals(ethernetSwitch.getEthernetSwitchMetrics())) {
                ethernetSwitch.setSwitchMetrics(this);
            }
        }
    }

    public void unlinkEthernetSwitch(EthernetSwitch ethernetSwitch) {
        if (Objects.equals(this.ethernetSwitch, ethernetSwitch)) {
            this.ethernetSwitch = null;
            if (ethernetSwitch != null) {
                ethernetSwitch.unlinkEthernetSwitchMetrics(this);
            }
        }
    }
}
