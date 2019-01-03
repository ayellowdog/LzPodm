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

package com.intel.podm.mappers;

import com.intel.podm.business.entities.redfish.DiscoverableEntity;
import com.intel.podm.client.resources.ExternalServiceResource;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

//@Dependent
@Component
public class MapperFinder {
	@Autowired
    private List<EntityMapper<? extends ExternalServiceResource, ? extends DiscoverableEntity>> mapperPool;

    private Collection<EntityMapper<? extends ExternalServiceResource, ? extends DiscoverableEntity>> cache;

    public Optional<EntityMapper<? extends ExternalServiceResource, ? extends DiscoverableEntity>> find(ExternalServiceResource resource) {
        return StreamSupport.stream(getMapperPool().spliterator(), false)
            .filter(mapper -> mapper.canMap(resource))
            .findFirst();
    }
    @PostConstruct
    private void init() {
    }
    private Iterable<EntityMapper<? extends ExternalServiceResource, ? extends DiscoverableEntity>> getMapperPool() {
        if (cache == null) {
            cache = stream(mapperPool.spliterator(), false).collect(toList());
        }

        return cache;
    }
}
