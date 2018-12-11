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

import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.intel.podm.business.entities.redfish.DiscoverableEntity;
import com.intel.podm.business.entities.redfish.ExternalLink;
import com.intel.podm.business.entities.redfish.ExternalService;

@Component
public class ExternalLinkDao {

    @Autowired
    private GenericDao genericDao;

//    @Transactional(MANDATORY)
    public void removeAll(ExternalService externalService, Predicate<ExternalLink> predicate) {
        List<DiscoverableEntity> affectedEntities = externalService
            .getOwnedLinks().stream()
            .map(ExternalLink::getDiscoverableEntity)
            .collect(toList());

        genericDao.removeAndClear(externalService.getOwnedLinks(), predicate);

        removeOrphanEntities(affectedEntities);
    }

    private void removeOrphanEntities(List<DiscoverableEntity> affectedEntities) {
        affectedEntities.stream()
            .filter(discoverableEntity -> discoverableEntity.getExternalLinks().isEmpty())
            .forEach(discoverableEntity -> genericDao.remove(discoverableEntity));
    }

    public <T extends DiscoverableEntity> ExternalLink create(URI sourceUri, ExternalService externalService, T discoverableEntity) {
        ExternalLink externalLink = genericDao.create(ExternalLink.class);
        externalLink.setSourceUri(sourceUri);
        externalLink.setExternalService(externalService);
        externalLink.setDiscoverableEntity(discoverableEntity);
        return externalLink;
    }

    public <T extends DiscoverableEntity> void createIfNotExisting(URI sourceUri, ExternalService externalService, T discoverableEntity) {
        if (discoverableEntity.getExternalLinks().stream().noneMatch(el -> externalService.equals(el.getExternalService()))) {
            create(sourceUri, externalService, discoverableEntity);
        }
    }
}
