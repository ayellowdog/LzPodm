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
import static com.inspur.podm.common.utils.Contracts.requiresNonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.inspur.podm.common.intel.types.Id;
import com.inspur.podm.common.intel.types.IndicatorLed;
import com.inspur.podm.common.intel.types.PowerState;
import com.inspur.podm.common.intel.types.Protocol;
import com.inspur.podm.common.intel.types.actions.ResetType;
import com.inspur.podm.common.persistence.base.Entity;
import com.inspur.podm.common.persistence.base.Resettable;
import static com.inspur.podm.common.utils.Contracts.requiresNonNull;
import static java.lang.String.format;
import static java.time.Duration.between;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static javax.persistence.EnumType.STRING;
import static org.hibernate.annotations.GenerationTime.INSERT;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Enumerated;

import com.inspur.podm.common.intel.types.Id;
import com.inspur.podm.common.intel.types.ServiceType;
import com.inspur.podm.common.persistence.base.Entity;
import com.inspur.podm.common.persistence.converter.IdToLongConverter;

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
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Generated;
@javax.persistence.Entity
@Table(name = "volume_metrics", indexes = @Index(name = "idx_volume_metrics_entity_id", columnList = "entity_id", unique = true))
public class VolumeMetrics extends DiscoverableEntity {

    @Column(name = "entity_id", columnDefinition = ENTITY_ID_STRING_COLUMN_DEFINITION)
    private Id entityId;

    @Column(name = "capacity_used_bytes")
    private Long capacityUsedBytes;

    @OneToOne(fetch = LAZY, cascade = {MERGE, PERSIST})
    @JoinColumn(name = "volume_id")
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
