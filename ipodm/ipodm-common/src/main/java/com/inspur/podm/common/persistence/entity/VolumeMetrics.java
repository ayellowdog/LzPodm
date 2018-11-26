/*
 * Copyright (c) 2018 Intel Corporation
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
//@Table(name = "volume_metrics", indexes = @Index(name = "idx_volume_metrics_entity_id", columnList = "entity_id", unique = true))
public class VolumeMetrics extends DiscoverableEntity {

/** @Fields serialVersionUID: TODO 功能描述  */
	private static final long serialVersionUID = -43545179754588044L;

	//    @Column(name = "entity_id", columnDefinition = ENTITY_ID_STRING_COLUMN_DEFINITION)
    private Id entityId;

//    @Column(name = "capacity_used_bytes")
    private Long capacityUsedBytes;

//    @OneToOne(fetch = LAZY, cascade = {MERGE, PERSIST})
//    @JoinColumn(name = "volume_id")
    private Volume volume;

    @Override
    public Id getId() {
        return entityId;
    }

    @Override
    public void setId(Id id) {
        entityId = id;
    }

    public Long getCapacityUsedBytes() {
        return capacityUsedBytes;
    }

    public void setCapacityUsedBytes(Long capacityUsedBytes) {
        this.capacityUsedBytes = capacityUsedBytes;
    }

    public Volume getVolume() {
        return volume;
    }

    public void setVolume(Volume volume) {
        if (!Objects.equals(this.volume, volume)) {
            unlinkVolume(this.volume);
            this.volume = volume;
            if (volume != null && !this.equals(volume.getMetrics())) {
                volume.setMetrics(this);
            }
        }
    }

    public void unlinkVolume(Volume volume) {
        if (Objects.equals(this.volume, volume)) {
            this.volume = null;
            if (volume != null) {
                volume.unlinkMetrics(this);
            }
        }
    }
    @Override
    public void preRemove() {
        unlinkVolume(volume);
    }

    @Override
    public boolean containedBy(Entity possibleParent) {
        return isContainedBy(possibleParent, volume);
    }
}
