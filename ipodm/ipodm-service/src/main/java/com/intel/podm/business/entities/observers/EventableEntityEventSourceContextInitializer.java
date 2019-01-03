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

package com.intel.podm.business.entities.observers;

import com.intel.podm.business.entities.EntityToUriConverter;
import com.intel.podm.business.entities.interceptors.EventOriginInfoProvider;
import com.intel.podm.business.entities.interceptors.EventSuppressions;
import com.intel.podm.business.entities.listeners.EntityAddedEvent;
import com.intel.podm.business.entities.redfish.DiscoverableEntity;
import com.intel.podm.business.entities.redfish.base.Entity;
import com.intel.podm.business.entities.redfish.base.MultiSourceResource;
import com.intel.podm.business.entities.resolvers.MultiSourceEntityResolver;
import com.intel.podm.business.entities.resolvers.MultiSourceEntityResolverProvider;
import com.intel.podm.business.entities.types.EntityAdded;
import com.intel.podm.common.logger.Logger;
import com.intel.podm.common.logger.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

import static javax.enterprise.event.TransactionPhase.BEFORE_COMPLETION;

//@ApplicationScoped
@Component
public class EventableEntityEventSourceContextInitializer {
    @Autowired
    private EntityToUriConverter entityToUriConverter;

    @Autowired
    private EventSuppressions eventSuppressions;

    @Autowired
    private EventOriginInfoProvider eventOriginInfoProvider;

    @Autowired
    private MultiSourceEntityResolverProvider multiSourceEntityResolverProvider;

//    @Inject
//    private Logger logger;
    private static final Logger logger = LoggerFactory.getLogger(EventableEntityEventSourceContextInitializer.class);
    
//    public void onEntityAdded(@EntityAdded @Observes(during = BEFORE_COMPLETION) Entity entity) {
    public void onEntityAdded(Entity entity) {
    	if (!eventSuppressions.isSuppressed(entity)) {
            fillMultiSourceDiscriminatorIfMultiSourceResource(entity);
            final Entity eventOriginEntity = eventOriginInfoProvider.findEventOrigin(entity);
            final Optional<URI> uriOptional = entityToUriConverter.entityToUri(eventOriginEntity);
            if (uriOptional.isPresent()) {
                entity.setEventSourceContext(uriOptional.get());
            } else {
                throw new IllegalStateException("Every entity should have its own EventOrigin");
            }
        }
    }
    //监听事件
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onEntityAddedNew(EntityAddedEvent event) {
    	Entity entity = event.getEntity();
    	onEntityAdded(entity);
    }

    private void fillMultiSourceDiscriminatorIfMultiSourceResource(Entity entity) {
        if (entity instanceof MultiSourceResource) {
            MultiSourceResource multiSourceResource = (MultiSourceResource) entity;
            Optional<MultiSourceEntityResolver> resolver = getMultiSourceEntityResolverFor(multiSourceResource);
            resolver.ifPresent(r -> initializeMultiSourceDiscriminator(multiSourceResource, r));
        }
    }

    private void initializeMultiSourceDiscriminator(MultiSourceResource multiSourceResource, MultiSourceEntityResolver r) {
        multiSourceResource.setMultiSourceDiscriminator(r.createMultiSourceDiscriminator((DiscoverableEntity) multiSourceResource));
    }

    private Optional<MultiSourceEntityResolver> getMultiSourceEntityResolverFor(MultiSourceResource multiSourceResource) {
        return multiSourceEntityResolverProvider.getCachedMultiSourceEntityResolvers().stream()
            .filter(multiSourceEntityResolver -> Objects.equals(multiSourceEntityResolver.getEntityClass(), multiSourceResource.getClass()))
            .findFirst();
    }
}
