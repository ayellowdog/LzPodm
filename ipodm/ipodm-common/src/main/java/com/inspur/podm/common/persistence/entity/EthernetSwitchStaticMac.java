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

package com.inspur.podm.common.persistence.entity;


import java.util.Objects;

import com.inspur.podm.common.intel.types.Id;
import com.inspur.podm.common.persistence.base.Entity;

//@javax.persistence.Entity
//@Table(name = "ethernet_switch_static_mac", indexes = @Index(name = "idx_ethernet_switch_static_mac_entity_id", columnList = "entity_id", unique = true))
//@Eventable
public class EthernetSwitchStaticMac extends DiscoverableEntity {
/** @Fields serialVersionUID: TODO 功能描述  */
	private static final long serialVersionUID = -5954897824492948626L;

//    @Column(name = "entity_id", columnDefinition = ENTITY_ID_STRING_COLUMN_DEFINITION)
    private Id entityId;

//    @Column(name = "mac_address")
    private String macAddress;

//    @Column(name = "vlan_id")
    private Integer vlanId;

//    @ManyToOne(fetch = LAZY, cascade = {MERGE, PERSIST})
//    @JoinColumn(name = "ethernet_switch_port_id")
    private EthernetSwitchPort ethernetSwitchPort;

    @Override
    public Id getId() {
        return entityId;
    }

    @Override
    public void setId(Id id) {
        entityId = id;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Integer getVlanId() {
        return vlanId;
    }

    public void setVlanId(Integer vlanId) {
        this.vlanId = vlanId;
    }

    public EthernetSwitchPort getEthernetSwitchPort() {
        return ethernetSwitchPort;
    }

    public void setEthernetSwitchPort(EthernetSwitchPort ethernetSwitchPort) {
        if (!Objects.equals(this.ethernetSwitchPort, ethernetSwitchPort)) {
            unlinkEthernetSwitchPort(this.ethernetSwitchPort);
            this.ethernetSwitchPort = ethernetSwitchPort;
            if (ethernetSwitchPort != null && !ethernetSwitchPort.getEthernetSwitchStaticMacs().contains(this)) {
                ethernetSwitchPort.addEthernetSwitchStaticMac(this);
            }
        }
    }

    public void unlinkEthernetSwitchPort(EthernetSwitchPort ethernetSwitchPort) {
        if (Objects.equals(this.ethernetSwitchPort, ethernetSwitchPort)) {
            this.ethernetSwitchPort = null;
            if (ethernetSwitchPort != null) {
                ethernetSwitchPort.unlinkEthernetSwitchStaticMac(this);
            }
        }
    }

    @Override
    public void preRemove() {
          unlinkEthernetSwitchPort(ethernetSwitchPort);
    }

    @Override
    public boolean containedBy(Entity possibleParent) {
        return isContainedBy(possibleParent, ethernetSwitchPort);
    }
}
