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

package com.inspur.podm.service.dao;

import static com.inspur.podm.common.persistence.entity.Manager.GET_ALL_MANAGER_IDS;
import static javax.transaction.Transactional.TxType.MANDATORY;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

import com.inspur.podm.common.intel.types.Id;
import com.inspur.podm.common.persistence.entity.Manager;

@ApplicationScoped
public class ManagerDao extends Dao<Manager> {
//    @Transactional(MANDATORY)
    public List<Id> getAllManagerIds() {
        return entityManager.createNamedQuery(GET_ALL_MANAGER_IDS, Id.class).getResultList();
    }
}
