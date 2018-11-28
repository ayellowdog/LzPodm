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


import static com.inspur.podm.common.intel.types.ComposedNodeState.ALLOCATED;
import static com.inspur.podm.common.intel.types.ComposedNodeState.ALLOCATING;
import static com.inspur.podm.common.intel.types.ComposedNodeState.ASSEMBLING;
import static com.inspur.podm.common.utils.Collector.toSingle;
import static com.inspur.podm.common.utils.Contracts.requiresNonNull;
import static com.inspur.podm.common.utils.Converters.convertGibToBytes;
import static java.math.BigDecimal.ZERO;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.LAZY;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.inspur.podm.common.intel.types.Id;
import com.inspur.podm.common.intel.types.Protocol;
import com.inspur.podm.common.persistence.base.Entity;
import com.inspur.podm.common.persistence.entity.embeddables.Capacity;
import com.inspur.podm.common.persistence.entity.embeddables.Identifier;
@javax.persistence.Entity
@Table(name = "storage_pool", indexes = @Index(name = "idx_storage_pool_entity_id", columnList = "entity_id", unique = true))
//@Eventable
//@SuppressWarnings({"checkstyle:MethodCount", "checkstyle:ClassFanOutComplexity"})
public class StoragePool extends DiscoverableEntity {

    @Column(name = "entity_id", columnDefinition = ENTITY_ID_STRING_COLUMN_DEFINITION)
    private Id entityId;

    @Embedded
    private Identifier identifier;

    @Column(name = "block_size_bytes")
    private BigDecimal blockSizeBytes;

    @Embedded
    private Capacity capacity;

    @OneToMany(mappedBy = "storagePool", fetch = LAZY, cascade = {MERGE, PERSIST})
    private Set<CapacitySource> capacitySources = new HashSet<>();

    @OneToMany(mappedBy = "storagePool", fetch = LAZY, cascade = {MERGE, PERSIST})
    private Set<Volume> allocatedVolumes = new HashSet<>();

    @OneToMany(mappedBy = "storagePool", fetch = LAZY, cascade = {MERGE, PERSIST})
    private Set<StoragePool> allocatedPools = new HashSet<>();

    @ManyToMany(fetch = LAZY, cascade = {MERGE, PERSIST})
    @JoinTable(
        name = "storage_providing_pool_capacity_source",
        joinColumns = {@JoinColumn(name = "storage_pool_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "capacity_source_id", referencedColumnName = "id")})
    private Set<CapacitySource> providingPoolCapacitySources = new HashSet<>();

    @ManyToMany(fetch = LAZY, cascade = {MERGE, PERSIST})
    @JoinTable(
        name = "storage_pool_composed_node",
        joinColumns = {@JoinColumn(name = "storage_pool_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "composed_node_id", referencedColumnName = "id")})
    private Set<ComposedNode> composedNodes = new HashSet<>();

    @ManyToOne(fetch = LAZY, cascade = {MERGE, PERSIST})
    @JoinColumn(name = "storage_pool_id")
    private StoragePool storagePool;

    @ManyToOne(fetch = LAZY, cascade = {MERGE, PERSIST})
    @JoinColumn(name = "storage_service_id")
    private StorageService storageService;

    @Override
    public Id getTheId() {
        return entityId;
    }

    @Override
    public void setTheId(Id id) {
        entityId = id;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public BigDecimal getBlockSizeBytes() {
        return blockSizeBytes;
    }

    public void setBlockSizeBytes(BigDecimal blockSizeBytes) {
        this.blockSizeBytes = blockSizeBytes;
    }

    public Capacity getCapacity() {
        return capacity;
    }

    public void setCapacity(Capacity capacity) {
        this.capacity = capacity;
    }

    public Set<CapacitySource> getCapacitySources() {
        return capacitySources;
    }

    public void addCapacitySource(CapacitySource sources) {
        requiresNonNull(sources, "capacitySources");

        capacitySources.add(sources);
        if (!this.equals(sources.getStoragePool())) {
            sources.setStoragePool(this);
        }
    }

    public void unlinkCapacitySource(CapacitySource sources) {
        if (capacitySources.contains(sources)) {
            capacitySources.remove(sources);
            if (sources != null) {
                sources.unlinkStoragePool(this);
            }
        }
    }

    public Set<Volume> getAllocatedVolumes() {
        return allocatedVolumes;
    }

    public void addAllocatedVolume(Volume volume) {
        requiresNonNull(volume, "volume");

        allocatedVolumes.add(volume);
        if (!this.equals(volume.getStoragePool())) {
            volume.setStoragePool(this);
        }
    }

    public void unlinkAllocatedVolume(Volume volume) {
        if (allocatedVolumes.contains(volume)) {
            allocatedVolumes.remove(volume);
            if (volume != null) {
                volume.unlinkStoragePool(this);
            }
        }
    }

    public Set<StoragePool> getAllocatedPools() {
        return allocatedPools;
    }

    public void addAllocatedPool(StoragePool storagePool) {
        requiresNonNull(storagePool, "storagePool");

        allocatedPools.add(storagePool);
        if (!this.equals(storagePool.getStoragePool())) {
            storagePool.setStoragePool(this);
        }
    }

    public void unlinkAllocatedPool(StoragePool storagePool) {
        if (allocatedPools.contains(storagePool)) {
            allocatedPools.remove(storagePool);
            if (storagePool != null) {
                storagePool.unlinkStoragePool(this);
            }
        }
    }

    public Set<CapacitySource> getProvidingPoolCapacitySources() {
        return providingPoolCapacitySources;
    }

    public void addProvidingPoolCapacitySource(CapacitySource providingPoolCapacitySource) {
        requiresNonNull(providingPoolCapacitySource, "providingPoolCapacitySource");

        providingPoolCapacitySources.add(providingPoolCapacitySource);
        if (!providingPoolCapacitySource.getProvidingPools().contains(this)) {
            providingPoolCapacitySource.addProvidingPool(this);
        }
    }

    public void unlinkProvidingPoolCapacitySource(CapacitySource providingPoolCapacitySource) {
        if (providingPoolCapacitySources.contains(providingPoolCapacitySource)) {
            providingPoolCapacitySources.remove(providingPoolCapacitySource);
            if (providingPoolCapacitySource != null) {
                providingPoolCapacitySource.unlinkProvidingPool(this);
            }
        }
    }

    public Set<ComposedNode> getComposedNodes() {
        return composedNodes;
    }

    public void addComposedNode(ComposedNode composedNode) {
        requiresNonNull(composedNode, "composedNode");

        composedNodes.add(composedNode);
        if (!composedNode.getStoragePools().contains(this)) {
            composedNode.addStoragePool(this);
        }
    }

    public void unlinkComposedNode(ComposedNode composedNode) {
        if (composedNodes.contains(composedNode)) {
            composedNodes.remove(composedNode);
            if (composedNode != null) {
                composedNode.unlinkStoragePool(this);
            }
        }
    }

    public StoragePool getStoragePool() {
        return storagePool;
    }

    public void setStoragePool(StoragePool storagePool) {
        this.storagePool = storagePool;
    }

    public void unlinkStoragePool(StoragePool storagePool) {
        if (Objects.equals(this.storagePool, storagePool)) {
            this.storagePool = null;
            if (storagePool != null) {
                storagePool.unlinkAllocatedPool(this);
            }
        }
    }

    public StorageService getStorageService() {
        return storageService;
    }

    public void setStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    public void unlinkStorageService(StorageService storageService) {
        if (Objects.equals(this.storageService, storageService)) {
            this.storageService = null;
            if (storageService != null) {
                storageService.unlinkStoragePool(this);
            }
        }
    }

    public BigDecimal getFreeSpace() {
        return getCapacity().getData().getAllocatedBytes()
            .subtract(getCapacity().getData().getConsumedBytes())
            .subtract(capacityOfVolumesFromNotAssembledComposedNodes());
    }

    public Protocol getProtocol() {
        return this.getService().getOwnedEntities()
            .stream()
            .filter(discoverableEntity -> discoverableEntity instanceof Fabric)
            .map(Fabric.class::cast)
            .map(Fabric::getFabricType)
            .collect(toSingle());
    }

    @Override
    public void preRemove() {
        unlinkCollection(capacitySources, this::unlinkCapacitySource);
        unlinkCollection(allocatedVolumes, this::unlinkAllocatedVolume);
        unlinkCollection(allocatedPools, this::unlinkAllocatedPool);
        unlinkCollection(providingPoolCapacitySources, this::unlinkProvidingPoolCapacitySource);
        unlinkCollection(composedNodes, this::unlinkComposedNode);
        unlinkStoragePool(storagePool);
        unlinkStorageService(storageService);
    }

    @Override
    public boolean containedBy(Entity possibleParent) {
        return isContainedBy(possibleParent, storageService);
    }

    private BigDecimal capacityOfVolumesFromNotAssembledComposedNodes() {
        BigDecimal capacityOfComposedNodesInGiB = composedNodes.stream()
            .filter(composedNode -> composedNode.isInAnyOfStates(ALLOCATING, ALLOCATED, ASSEMBLING))
            .map(ComposedNode::getRemoteDriveCapacityGib)
            .reduce(ZERO, BigDecimal::add);
        return convertGibToBytes(capacityOfComposedNodesInGiB);
    }
}
