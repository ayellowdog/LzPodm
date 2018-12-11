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

package com.intel.podm.discovery.external.matcher;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.intel.podm.business.entities.dao.DiscoverableEntityDao;
import com.intel.podm.business.entities.dao.ExternalLinkDao;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.DiscoverableEntity;
import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.client.WebClientRequestException;
import com.intel.podm.client.resources.ExternalServiceResource;
import com.intel.podm.client.resources.redfish.ComputerSystemResource;
//@PostConstruct方法已被注释！！！！！！！！！！！！！！！！
//@Dependent
//@SuppressWarnings({"checkstyle:ClassFanOutComplexity"})
@Component
public class EntityObtainer {
//	@Autowired
    private Instance<EntityObtainerHelper<? extends ExternalServiceResource>> helpers;

    private List<EntityObtainerHelper<? extends ExternalServiceResource>> cachedHelpers;

    @Autowired
    private ComputerSystemFinder computerSystemFinder;

    @Autowired
    private ExternalLinkDao externalLinkDao;

    @Autowired
    private DiscoverableEntityDao discoverableEntityDao;

    private static final Logger logger = LoggerFactory.getLogger(EntityObtainer.class);

    @SuppressWarnings({"unchecked"})
//    @Transactional(MANDATORY)
    @org.springframework.transaction.annotation.Transactional
    public DiscoverableEntity obtain(ExternalService service, ExternalServiceResource resource) {
        EntityObtainerHelper entityObtainerHelper = getHelper(resource);
        if (entityObtainerHelper == null) {
            return null;
        }

        Optional<ComputerSystemResource> parentSystem = findParentComputerSystem(resource, entityObtainerHelper);
        if (parentSystem.isPresent()) {
            ComputerSystem computerSystem = computerSystemFinder.findByCorrelatedPsmeComputerSystem(parentSystem.get());

            DiscoverableEntity entity = (DiscoverableEntity) entityObtainerHelper.findEntityFor(computerSystem, resource)
                .orElseGet(() -> discoverableEntityDao.createEntity(
                    service,
                    resource.getGlobalId(service.getTheId()),
                    entityObtainerHelper.getEntityClass())
                );

            externalLinkDao.createIfNotExisting(resource.getUri(), service, entity);
            return entity;
        } else {
            return null;
        }
    }

    @PostConstruct
    private void init() {
//        cachedHelpers = stream(helpers.spliterator(), false).collect(toList());
    }

    @SuppressWarnings({"unchecked"})
    private Optional<ComputerSystemResource> findParentComputerSystem(ExternalServiceResource resource, EntityObtainerHelper helper) {
        try {
            return of(helper.findComputerSystemResourceFor(resource));
        } catch (WebClientRequestException e) {
            logger.error(format("Parent ComputerSystem resource has not been found for '%s'", resource.getUri()), e);
            return empty();
        }
    }

    private EntityObtainerHelper getHelper(ExternalServiceResource resource) {
        return cachedHelpers.stream()
            .filter(helper -> helper.getResourceClass().isInstance(resource))
            .findFirst().orElse(null);
    }
}
