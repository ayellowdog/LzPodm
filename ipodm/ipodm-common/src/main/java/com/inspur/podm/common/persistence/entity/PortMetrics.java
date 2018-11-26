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
//@Table(name = "port_metrics", indexes = @Index(name = "idx_port_metrics_entity_id", columnList = "entity_id", unique = true))
//@Eventable
public class PortMetrics extends DiscoverableEntity {
/** @Fields serialVersionUID: TODO 功能描述  */
	private static final long serialVersionUID = 2374322929072682980L;

//    @Column(name = "entity_id", columnDefinition = ENTITY_ID_STRING_COLUMN_DEFINITION)
    private Id entityId;

//    @Column(name = "health")
    private String health;

//    @OneToOne(fetch = LAZY, cascade = {MERGE, PERSIST})
//    @JoinColumn(name = "port_id")
    private Port port;

    @Override
    public Id getId() {
        return entityId;
    }

    @Override
    public void setId(Id id) {
        this.entityId = id;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public Port getPort() {
        return port;
    }

    public void setPort(Port port) {
        if (!Objects.equals(this.port, port)) {
            unlinkPort(this.port);
            this.port = port;
            if (port != null && !this.equals(port.getPortMetrics())) {
                port.setPortMetrics(this);
            }
        }
    }

    public void unlinkPort(Port port) {
        if (Objects.equals(this.port, port)) {
            this.port = null;
            if (port != null) {
                port.unlinkMetrics(this);
            }
        }
    }

    @Override
    public void preRemove() {
        unlinkPort(port);
    }

    @Override
    public boolean containedBy(Entity possibleParent) {
        return isContainedBy(possibleParent, port);
    }
}
