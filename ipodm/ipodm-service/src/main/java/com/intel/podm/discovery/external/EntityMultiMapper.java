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

import com.intel.podm.business.entities.dao.DiscoverableEntityDao;
import com.intel.podm.business.entities.dao.ExternalLinkDao;
import com.intel.podm.business.entities.redfish.DiscoverableEntity;
import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.client.resources.ExternalServiceResource;
import com.intel.podm.common.types.Id;
import com.intel.podm.config.base.Config;
import com.intel.podm.config.base.ConfigProvider;
import com.intel.podm.config.base.Holder;
import com.intel.podm.config.base.dto.DiscoveryConfig;
import com.intel.podm.discovery.external.matcher.ComputerSystemFinder;
import com.intel.podm.discovery.external.matcher.EntityObtainer;
import com.intel.podm.mappers.Mapper;
import com.intel.podm.mappers.MapperFinder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.intel.podm.common.types.ServiceType.LUI;
import static com.intel.podm.common.utils.Contracts.requires;
import static java.util.Objects.nonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static javax.transaction.Transactional.TxType.MANDATORY;

//@ApplicationScoped
@Component
public class EntityMultiMapper {
    @Autowired
    private EntityObtainer entityObtainer;

    @Autowired
    private ExternalLinkDao externalLinkDao;

    @Autowired
    private DiscoverableEntityDao discoverableEntityDao;

    private static final Logger logger = LoggerFactory.getLogger(EntityMultiMapper.class);

//    @Autowired
//    @Config
//    private Holder<DiscoveryConfig> config;
//    private ConfigProvider config;

    @Autowired
    private MapperFinder mapperFinder;

//    @Transactional(MANDATORY)
    @Transactional(propagation = Propagation.MANDATORY)
    public Map<ExternalServiceResource, DiscoverableEntity> map(Collection<ExternalServiceResource> resources, ExternalService externalService) {
        requires(nonNull(externalService), "externalService should not be null");
        Map<ExternalServiceResource, DiscoverableEntity> result = new HashMap<>();

        for (ExternalServiceResource resource : resources) {
            DiscoverableEntity entity = map(resource, externalService);
            result.put(resource, entity);
        }
        //删除陈旧的entity
        deleteStaleEntities(resources, externalService);

        return result;
    }

    private void deleteStaleEntities(Collection<ExternalServiceResource> resources, ExternalService externalService) {
        Collection<Id> resourceIds = resources.stream().map(r -> r.getGlobalId(externalService.getId())).collect(toSet());
        externalLinkDao.removeAll(externalService, el -> !resourceIds.contains(el.getDiscoverableEntity().getGlobalId()));
    }

//    @Transactional(MANDATORY)
    //之所以需要事务，是因为128行可能会创建discoverableEntity
    @Transactional(propagation = Propagation.MANDATORY)
    public DiscoverableEntity map(ExternalServiceResource resource, ExternalService service) {
        return mapperFinder.find(resource)
            .flatMap(mapper -> mapWithMapper(resource, service, mapper))
            .orElse(null);
    }

    private Optional<DiscoverableEntity> mapWithMapper(ExternalServiceResource resource, ExternalService service, Mapper mapper) {
        try {
            return matchResourceByServiceType(resource, service, mapper.getTargetClass())
                .map(entity -> {
                    mapper.map(resource, entity);
                    return entity;
                });
        } catch (IllegalStateException e) {
            logger.error("Problem while matching resource: '{}'\n{}", resource.getUri(), e.getMessage());
            return empty();
        }
    }

    private Optional<DiscoverableEntity> matchResourceByServiceType(ExternalServiceResource resource, ExternalService service, Class targetClass) {
        Id entityId = resource.getGlobalId(service.getId());
        return ofNullable(
            Objects.equals(LUI, service.getServiceType())
                ? entityObtainer.obtain(service, resource)
                : discoverableEntityDao.findOrCreateEntity(service, entityId, resource.getUri(), targetClass)
        );
    }
}
