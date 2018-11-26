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
import com.inspur.podm.common.persistence.entity.embeddables.Ethernet;
import com.inspur.podm.common.persistence.entity.embeddables.IscsiBoot;


//@javax.persistence.Entity
//@Table(name = "network_device_function", indexes = @Index(name = "idx_network_device_function_entity_id", columnList = "entity_id", unique = true))
public class NetworkDeviceFunction extends DiscoverableEntity {
/** @Fields serialVersionUID: TODO 功能描述  */
	private static final long serialVersionUID = -3153471057662150278L;

//    @Column(name = "entity_id", columnDefinition = ENTITY_ID_STRING_COLUMN_DEFINITION)
    private Id entityId;

//    @Column(name = "device_enabled")
    private Boolean deviceEnabled;

//    @Embedded
    private Ethernet ethernet;

//    @Embedded
    private IscsiBoot iscsiBoot;

//    @ManyToOne(fetch = LAZY, cascade = {MERGE, PERSIST})
//    @JoinColumn(name = "network_interface_id")
    private NetworkInterface networkInterface;

    @Override
    public Id getId() {
        return entityId;
    }

    @Override
    public void setId(Id id) {
        this.entityId = id;
    }

    public Boolean isDeviceEnabled() {
        return deviceEnabled;
    }

    public void setDeviceEnabled(Boolean deviceEnabled) {
        this.deviceEnabled = deviceEnabled;
    }

    public Ethernet getEthernet() {
        return ethernet;
    }

    public void setEthernet(Ethernet ethernet) {
        this.ethernet = ethernet;
    }

    public IscsiBoot getIscsiBoot() {
        return iscsiBoot;
    }

    public void setIscsiBoot(IscsiBoot iscsiBoot) {
        this.iscsiBoot = iscsiBoot;
    }

    public NetworkInterface getNetworkInterface() {
        return networkInterface;
    }

    public void setNetworkInterface(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
    }

    public void unlinkNetworkInterface(NetworkInterface networkInterface) {
        if (Objects.equals(this.networkInterface, networkInterface)) {
            this.networkInterface = null;
            if (networkInterface != null) {
                networkInterface.unlinkNetworkDeviceFunction(this);
            }
        }
    }

    @Override
    public boolean containedBy(Entity possibleParent) {
        return isContainedBy(possibleParent, networkInterface);
    }

    @Override
    public void preRemove() {
        unlinkNetworkInterface(networkInterface);
    }
}
