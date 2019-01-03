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

import static com.inspur.podm.api.business.dto.redfish.CollectionDto.Type.EVENT_SUBSCRIPTION;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.ContextResolvingException;
import com.inspur.podm.api.business.dto.EventSubscriptionDto;
import com.inspur.podm.api.business.dto.redfish.CollectionDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.intel.podm.business.entities.EntityNotFoundException;
import com.intel.podm.business.entities.dao.GenericDao;
import com.intel.podm.business.entities.redfish.EventSubscription;
import com.intel.podm.business.redfish.services.Contexts;
import com.intel.podm.business.redfish.services.mappers.EntityToDtoMapper;

@Service("EventSubscriptionService")
class EventSubscriptionServiceImpl implements ReaderService<EventSubscriptionDto> {
    @Autowired
    private GenericDao genericDao;

    @Autowired
    private EntityToDtoMapper entityToDtoMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public CollectionDto getCollection(Context eventServiceContext) throws ContextResolvingException {
        List<Context> contexts = genericDao.findAll(EventSubscription.class).stream().map(Contexts::toContext).sorted().collect(toList());
        return new CollectionDto(EVENT_SUBSCRIPTION, contexts);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public EventSubscriptionDto getResource(Context context) throws ContextResolvingException {
        try {
            EventSubscription eventSubscription = genericDao.find(EventSubscription.class, context.getId());
            return (EventSubscriptionDto) entityToDtoMapper.map(eventSubscription);
        } catch (EntityNotFoundException e) {
            throw new ContextResolvingException(e.getMessage(), context, e);
        }
    }
}
