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

package com.intel.podm.business.redfish.services.impl;

import static com.intel.podm.business.redfish.services.helpers.SubscriptionRequestValidator.validateEventSubscriptionRequest;
import static com.inspur.podm.api.business.services.context.Context.contextOf;
import static com.inspur.podm.api.business.services.context.ContextType.EVENT_SERVICE;
import static com.inspur.podm.api.business.services.context.ContextType.EVENT_SUBSCRIPTION;
import static com.intel.podm.common.types.Id.id;
import static com.intel.podm.common.utils.Contracts.requiresNonNull;

import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.ContextResolvingException;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.CreationService;
import com.inspur.podm.api.business.services.redfish.RemovalService;
import com.inspur.podm.api.business.services.redfish.odataid.ODataId;
import com.inspur.podm.api.business.services.redfish.requests.EventSubscriptionRequest;
import com.intel.podm.business.entities.EntityNotFoundException;
import com.intel.podm.business.entities.dao.GenericDao;
import com.intel.podm.business.entities.redfish.EventSubscription;

@Service("SubscriptionActionService")
public class SubscriptionActionServiceImpl implements CreationService<EventSubscriptionRequest>, RemovalService<EventSubscriptionRequest> {
    private final GenericDao genericDao;

    @Autowired
    public SubscriptionActionServiceImpl(GenericDao genericDao) {
        requiresNonNull(genericDao, "genericDao");
        this.genericDao = genericDao;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void perform(Context context) throws BusinessApiException, TimeoutException {
        try {
            EventSubscription subscription = genericDao.find(EventSubscription.class, context.getId());
            genericDao.remove(subscription);
        } catch (EntityNotFoundException e) {
            throw new ContextResolvingException(e.getMessage(), context, e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Context create(Context context, EventSubscriptionRequest request) throws BusinessApiException, TimeoutException {
        EventSubscription eventSubscription = createSubscriptionEntity(validateEventSubscriptionRequest(request));
        // TODO: RSASW-8103
        Context subscriptionContext = contextOf(id(""), EVENT_SERVICE);
        return subscriptionContext.child(eventSubscription.getId(), EVENT_SUBSCRIPTION);
    }

    private EventSubscription createSubscriptionEntity(final EventSubscriptionRequest request) {
        EventSubscription eventSubscription = genericDao.create(EventSubscription.class);

        eventSubscription.setName(request.getName());
        eventSubscription.setProtocol(request.getProtocol());
        eventSubscription.setSubscriptionContext(request.getSubscriptionContext());
        eventSubscription.setDescription(request.getDescription());
        eventSubscription.setDestination(request.getDestination().toString());
        request.getEventTypes().forEach(eventSubscription::addEventType);
        if (request.getOriginResources() != null) {
            request.getOriginResources().stream()
                .map(ODataId::toString)
                .forEach(eventSubscription::addOriginResource);
        }

        return eventSubscription;
    }

}
