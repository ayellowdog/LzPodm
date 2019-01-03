/*
 * Copyright (c) 2016-2018 Intel Corporation
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

import static com.intel.podm.business.entities.redfish.Processor.GET_PROCESSOR_MULTI_SOURCE;
import static com.intel.podm.common.utils.IterableHelper.singleOrNull;
import static java.util.Optional.ofNullable;
import static javax.transaction.Transactional.TxType.MANDATORY;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.redfish.Processor;

//@ApplicationScoped
@Component
public class ProcessorDao extends Dao<Processor> {
//    @Transactional(MANDATORY)
    @Transactional(propagation = Propagation.MANDATORY)
    public List<Processor> findComplementaryProcessors(Processor processor) {
        TypedQuery<Processor> query = entityManager.createNamedQuery(GET_PROCESSOR_MULTI_SOURCE, Processor.class);
        query.setParameter("multiSourceDiscriminator", processor.getMultiSourceDiscriminator());
        query.setParameter("isComplementary", true);
        return query.getResultList();
    }

//    @Transactional(MANDATORY)
    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<Processor> findPrimaryProcessor(Processor complementaryProcessor) {
        TypedQuery<Processor> query = entityManager.createNamedQuery(GET_PROCESSOR_MULTI_SOURCE, Processor.class);
        query.setParameter("multiSourceDiscriminator", complementaryProcessor.getMultiSourceDiscriminator());
        query.setParameter("isComplementary", false);
        return ofNullable(singleOrNull(query.getResultList()));
    }
}
