/*
 * Copyright (c) 2015-2018 Intel Corporation
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

package com.inspur.podm.service.dao;

import static com.inspur.podm.common.persistence.entity.ComputerSystem.GET_COMPUTER_SYSTEMS_AVAILABLE_TO_ALLOCATE;
import static com.inspur.podm.common.persistence.entity.ComputerSystem.GET_COMPUTER_SYSTEMS_MATCHING_CONNECTION_ID;
import static com.inspur.podm.common.persistence.entity.ComputerSystem.GET_COMPUTER_SYSTEM_IDS_FROM_PRIMARY_DATA_SOURCE;
import static com.inspur.podm.common.persistence.entity.ComputerSystem.GET_COMPUTER_SYSTEM_MULTI_SOURCE;
import static com.inspur.podm.common.persistence.entity.ComputerSystem.GET_PHYSICAL_COMPUTER_SYSTEM_BY_UUID;
import static com.inspur.podm.common.persistence.entity.ComputerSystem.GET_PRIMARY_COMPUTER_SYSTEM;
import static com.inspur.podm.common.intel.types.SystemType.PHYSICAL;
import static com.inspur.podm.common.intel.types.SystemType.VIRTUAL;
import static com.inspur.podm.common.utils.IterableHelper.getUniqueValueFromSingletonListOrNull;
import static com.inspur.podm.common.utils.IterableHelper.optionalSingle;
import static com.inspur.podm.common.utils.IterableHelper.singleOrNull;
import static java.util.Optional.ofNullable;
import static javax.transaction.Transactional.TxType.MANDATORY;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import com.inspur.podm.common.intel.types.Id;
import com.inspur.podm.common.persistence.entity.ComputerSystem;

@ApplicationScoped
public class ComputerSystemDao extends Dao<ComputerSystem> {
    @Transactional(MANDATORY)
    public List<Id> findAllComputerSystemsFromPrimaryDataSource() {
        return entityManager.createNamedQuery(GET_COMPUTER_SYSTEM_IDS_FROM_PRIMARY_DATA_SOURCE, Id.class).getResultList();
    }

    @Transactional(MANDATORY)
    public Optional<ComputerSystem> findPrimarySystem(ComputerSystem complementarySystem) {
        return findPrimarySystem(complementarySystem.getUuid());
    }

    @Transactional(MANDATORY)
    public Optional<ComputerSystem> findPrimarySystem(UUID complementarySystemUuid) {
        TypedQuery<ComputerSystem> query = entityManager.createNamedQuery(GET_PRIMARY_COMPUTER_SYSTEM, ComputerSystem.class);
        query.setParameter("uuid", complementarySystemUuid);
        query.setParameter("systemTypeToBeExcluded", VIRTUAL);
        return ofNullable(singleOrNull(query.getResultList()));
    }

    @Transactional(MANDATORY)
    public List<ComputerSystem> findComplementarySystems(ComputerSystem computerSystem) {
        TypedQuery<ComputerSystem> query = entityManager.createNamedQuery(GET_COMPUTER_SYSTEM_MULTI_SOURCE, ComputerSystem.class);
        query.setParameter("uuid", computerSystem.getUuid());
        query.setParameter("isComplementary", true);
        return query.getResultList();
    }

    @Transactional(MANDATORY)
    public List<ComputerSystem> getComputerSystemsPossibleToAllocate() {
        TypedQuery<ComputerSystem> query = entityManager.createNamedQuery(GET_COMPUTER_SYSTEMS_AVAILABLE_TO_ALLOCATE, ComputerSystem.class);
        query.setParameter("allocated", false);
        query.setParameter("isComplementary", false);
        query.setParameter("systemType", PHYSICAL);
        return query.getResultList();
    }

    @Transactional(MANDATORY)
    public ComputerSystem tryGetUniqueComputerSystemByPcieConnectionId(String connectionId) {
        TypedQuery<ComputerSystem> query = entityManager.createNamedQuery(GET_COMPUTER_SYSTEMS_MATCHING_CONNECTION_ID, ComputerSystem.class);
        query.setParameter("pcieConnectionId", connectionId);
        return getUniqueValueFromSingletonListOrNull(query.getResultList());
    }

    @Transactional(MANDATORY)
    public Optional<ComputerSystem> getUniquePhysicalComputerSystemByUuid(UUID uuid) {
        TypedQuery<ComputerSystem> query = entityManager.createNamedQuery(GET_PHYSICAL_COMPUTER_SYSTEM_BY_UUID, ComputerSystem.class);
        query.setParameter("uuid", uuid);
        query.setParameter("systemType", PHYSICAL);
        return optionalSingle(query.getResultList());
    }
}
