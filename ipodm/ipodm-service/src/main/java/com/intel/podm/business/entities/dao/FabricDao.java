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

package com.intel.podm.business.entities.dao;

import static com.intel.podm.business.entities.redfish.Fabric.GET_ALL_FABRIC_IDS;
import static javax.transaction.Transactional.TxType.MANDATORY;

import java.util.List;

import javax.enterprise.context.Dependent;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.redfish.Fabric;
import com.intel.podm.common.types.Id;

@Dependent
//@Transactional(MANDATORY)
@Transactional(propagation = Propagation.MANDATORY)
public class FabricDao extends Dao<Fabric> {
    public List<Id> getAllFabricIds() {
        return entityManager.createNamedQuery(GET_ALL_FABRIC_IDS, Id.class).getResultList();
    }
}
