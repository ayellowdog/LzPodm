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


import static com.inspur.podm.common.utils.Contracts.requiresNonNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.inspur.podm.common.intel.types.Id;
import com.inspur.podm.common.persistence.BaseEntity;

//@javax.persistence.Entity
//@Table(name = "network_interface", indexes = @Index(name = "idx_network_interface_entity_id", columnList = "entity_id", unique = true))
public class NetworkInterface extends DiscoverableEntity {
/** @Fields serialVersionUID: TODO 功能描述  */
	private static final long serialVersionUID = 820513542015008809L;

//    @Column(name = "entity_id", columnDefinition = ENTITY_ID_STRING_COLUMN_DEFINITION)
    private Id entityId;

//    @OneToMany(mappedBy = "networkInterface", fetch = LAZY, cascade = {MERGE, PERSIST})
    private Set<NetworkDeviceFunction> networkDeviceFunctions = new HashSet<>();

//    @ManyToOne(fetch = LAZY, cascade = {MERGE, PERSIST})
//    @JoinColumn(name = "computer_system_id")
    private ComputerSystem computerSystem;

    @Override
    public Id getId() {
        return entityId;
    }

    @Override
    public void setId(Id id) {
        entityId = id;
    }

    public Set<NetworkDeviceFunction> getNetworkDeviceFunctions() {
        return networkDeviceFunctions;
    }

    public void addNetworkDeviceFunction(NetworkDeviceFunction networkDeviceFunction) {
        requiresNonNull(networkDeviceFunction, "networkDeviceFunction");

        networkDeviceFunctions.add(networkDeviceFunction);
        if (!this.equals(networkDeviceFunction.getNetworkInterface())) {
            networkDeviceFunction.setNetworkInterface(this);
        }
    }

    public void unlinkNetworkDeviceFunction(NetworkDeviceFunction networkDeviceFunction) {
        if (networkDeviceFunctions.contains(networkDeviceFunction)) {
            networkDeviceFunctions.remove(networkDeviceFunction);
            if (networkDeviceFunction != null) {
                networkDeviceFunction.unlinkNetworkInterface(this);
            }
        }
    }

    public ComputerSystem getComputerSystem() {
        return computerSystem;
    }

    public void setComputerSystem(ComputerSystem computerSystem) {
        this.computerSystem = computerSystem;
    }

    public void unlinkComputerSystem(ComputerSystem computerSystem) {
        if (Objects.equals(this.computerSystem, computerSystem)) {
            this.computerSystem = null;
            if (computerSystem != null) {
                computerSystem.unlinkNetworkInterface(this);
            }
        }
    }

    @Override
    public void preRemove() {
        unlinkCollection(networkDeviceFunctions, this::unlinkNetworkDeviceFunction);
        unlinkComputerSystem(computerSystem);
    }

    @Override
    public boolean containedBy(BaseEntity possibleParent) {
        return isContainedBy(possibleParent, computerSystem);
    }

}
