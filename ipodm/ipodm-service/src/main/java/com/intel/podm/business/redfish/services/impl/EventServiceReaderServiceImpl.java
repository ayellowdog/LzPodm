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

import static com.inspur.podm.api.business.services.redfish.odataid.ODataIdFromContextHelper.asOdataId;
import static com.inspur.podm.api.business.services.redfish.odataid.ODataIdHelper.oDataIdFromUri;
import static com.intel.podm.common.types.Status.statusFromString;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inspur.podm.api.business.ContextResolvingException;
import com.inspur.podm.api.business.dto.EventServiceDto;
import com.inspur.podm.api.business.dto.redfish.CollectionDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.inspur.podm.api.business.services.redfish.odataid.ODataId;
import com.intel.podm.common.types.events.EventType;
import com.intel.podm.config.base.DynamicHolder;
import com.intel.podm.config.base.dto.EventsConfig;

@Service("EventServiceReaderService")
public class EventServiceReaderServiceImpl implements ReaderService<EventServiceDto> {

    private EventsConfig eventsConfig;
    
    @Autowired
    public EventServiceReaderServiceImpl(DynamicHolder<EventsConfig> eventsConfigHolder) {
        this.eventsConfig = eventsConfigHolder.get(EventsConfig.class);
    }

    @Override
    public CollectionDto getCollection(Context context) throws ContextResolvingException {
        throw new UnsupportedOperationException("EventService is a singleton resource!");
    }

    @Override
    public EventServiceDto getResource(Context context) throws ContextResolvingException {
        ODataId oDataId = asOdataId(context);
        EventServiceDto eventService = new EventServiceDto();
        eventService.setId("EventService");
        eventService.setName("EventService");
        eventService.setDescription("EventService");
        eventService.setServiceEnabled(true);
        eventService.setDeliveryRetryAttempts(eventsConfig.getNorthboundConfiguration().getDeliveryRetryAttempts());
        eventService.setDeliveryRetryIntervalSeconds(eventsConfig.getNorthboundConfiguration().getDeliveryRetryIntervalSeconds());
        eventService.setEventTypesForSubscription(
            stream(EventType.values())
                .collect(toList())
        );
        eventService.setStatus(statusFromString("State=Enabled,Health=OK"));
        eventService.setSubscriptions(oDataIdFromUri(URI.create(oDataId + "/Subscriptions")));

        return eventService;
    }
}
