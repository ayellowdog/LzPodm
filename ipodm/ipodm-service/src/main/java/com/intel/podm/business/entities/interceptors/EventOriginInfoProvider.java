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

package com.intel.podm.business.entities.interceptors;

import static com.google.common.cache.CacheBuilder.newBuilder;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.cache.LoadingCache;
import com.intel.podm.business.entities.redfish.base.Entity;
import com.intel.podm.business.entities.resolvers.MultiSourceEntityResolverProvider;

//@Dependent
@Component
public class EventOriginInfoProvider {
    private static final int CACHE_SIZE = 1000;

    private LoadingCache<Class<?>, Function<Entity, Entity>> cache;

    @Autowired
    public EventOriginInfoProvider(MultiSourceEntityResolverProvider multiSourceEntityResolverProvider) {
        this.cache = newBuilder()
            .maximumSize(CACHE_SIZE)
            .build(new EventOriginInfoProviderCacheLoader(multiSourceEntityResolverProvider.getCachedMultiSourceEntityResolvers()));
    }

    public Entity findEventOrigin(Entity sourceEntity) {
        return cache.getUnchecked(sourceEntity.getClass()).apply(sourceEntity);
    }
}
