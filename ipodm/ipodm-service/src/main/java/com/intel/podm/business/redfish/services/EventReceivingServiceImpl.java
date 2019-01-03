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

package com.intel.podm.business.redfish.services;

import static java.lang.String.format;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.EventDispatchingException;
import com.inspur.podm.api.business.services.redfish.EventReceivingService;
//import com.intel.podm.business.EventDispatchingException;
import com.intel.podm.business.entities.dao.ExternalServiceDao;
import com.intel.podm.business.entities.redfish.ExternalService;
//import com.intel.podm.business.services.redfish.EventReceivingService;
import com.intel.podm.common.types.redfish.RedfishEventArray;

//@ApplicationScoped
@Service("EventReceivingService")
class EventReceivingServiceImpl implements EventReceivingService {
	@Autowired
	private ExternalServiceDao externalServiceDao;

	@Autowired
    private EventsProcessor activeProcessor;

    @Override
//    @Transactional(REQUIRES_NEW)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void dispatch(UUID originatingServiceUuid, RedfishEventArray eventArray) throws EventDispatchingException {
        ExternalService service = externalServiceDao.tryGetUniqueExternalServiceByUuid(originatingServiceUuid);
        if (service == null) {
            throw new EventDispatchingException(format("Service with UUID: %s does not exist.", originatingServiceUuid));
        }

        activeProcessor.handle(service.getUuid(), eventArray);
    }

//    @ApplicationScoped
//    @Produces
//    public EventsProcessor createEventProcessor(@Config Holder<EventsConfig> eventsConfig,
//                                                IncomingEventsProcessor incomingEventsProcessor,
//                                                AutoEvictingIncomingEventsBuffer autoEvictingIncomingEventsBuffer) {
//
//        SouthboundConfiguration southboundEventingConfig = eventsConfig.get().getSouthboundConfiguration();
//        EventsProcessor activeEventProcessor;
//        if (southboundEventingConfig.isBufferedEventProcessingEnabled()) {
//            BufferedEventProcessing bufferedEventProcessingConfig = southboundEventingConfig.getBufferedEventProcessing();
//            autoEvictingIncomingEventsBuffer.scheduleEvictionAtFixedRate(bufferedEventProcessingConfig.getProcessingWindowSizeInSeconds());
//            activeEventProcessor = (serviceUuid, events) -> autoEvictingIncomingEventsBuffer.handle(serviceUuid, events);
//        } else {
//            activeEventProcessor = (serviceUuid, events) -> incomingEventsProcessor.handle(serviceUuid, events);
//        }
//        return activeEventProcessor;
//    }
}
