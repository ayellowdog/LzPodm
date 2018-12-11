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

package com.intel.podm.discovery.external;

import static com.intel.podm.common.utils.Contracts.requiresNonNull;
import static java.lang.String.format;
import static javax.transaction.Transactional.TxType.MANDATORY;

import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.intel.podm.business.entities.dao.ExternalServiceDao;
import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.common.types.discovery.ServiceEndpoint;

//@Dependent
@Component
//@Lazy
public class ExternalServiceRepository {
    @Autowired
    private ExternalServiceDao externalServiceDao;

//    @Transactional(MANDATORY)
    @org.springframework.transaction.annotation.Transactional
    public ExternalService find(UUID uuid) {
        ExternalService service = findOrNull(uuid);
        if (service == null) {
            String msg = format("there is no service with UUID '%s'", uuid);
            throw new IllegalStateException(msg);
        }

        return service;
    }

//    @Transactional(MANDATORY)
    @org.springframework.transaction.annotation.Transactional
    public ExternalService findOrNull(UUID uuid) {
        requiresNonNull(uuid, "uuid");
        return externalServiceDao.tryGetUniqueExternalServiceByUuid(uuid);
    }

//    @Transactional(MANDATORY)
    @org.springframework.transaction.annotation.Transactional
    public ExternalService create(ServiceEndpoint endpoint) {
        UUID uuid = endpoint.getServiceUuid();

        requiresNonNull(uuid, "uuid");

        if (findOrNull(uuid) != null) {
            String msg = format("service with UUID '%s' exists", uuid);
            throw new IllegalStateException(msg);
        }

        ExternalService service = externalServiceDao.create();
        service.setUuid(uuid);
        service.setServiceType(endpoint.getServiceType());
        return service;
    }
}
