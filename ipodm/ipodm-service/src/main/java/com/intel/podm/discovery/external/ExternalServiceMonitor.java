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

import com.inspur.podm.common.context.AppContext;
import com.inspur.podm.common.context.ApplicationContextUtil;
import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.discovery.external.event.EventSubscriptionMonitor;

import javax.ejb.AccessTimeout;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

import static com.intel.podm.discovery.external.ExternalServiceMonitoringEvent.externalServiceMonitoringStartedEvent;
import static com.intel.podm.discovery.external.ExternalServiceMonitoringEvent.externalServiceMonitoringStoppedEvent;
import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.ejb.LockType.WRITE;
import static javax.transaction.Transactional.TxType.SUPPORTS;

//@Singleton
@Component
@Lazy
public class ExternalServiceMonitor {
    @Autowired
    private ExternalServiceRepository externalServiceRepository;

    @Autowired
    private ScheduledDiscoveryManager scheduledDiscoveryManager;

    @Autowired
    private EventSubscriptionMonitor eventSubscriptionMonitor;

//    @Autowired
//    private BeanManager beanManager;

    /**
     * LockType.WRITE used due to concurrent access to discovery manager and subscription monitor.
     */
//    @Lock(WRITE)
//    @Transactional(SUPPORTS)
//    @AccessTimeout(value = 5, unit = SECONDS)
    @Transactional(propagation = Propagation.SUPPORTS, timeout = 5)
    public synchronized void monitorService(UUID serviceUuid) {
        ExternalService service = externalServiceRepository.find(serviceUuid);
        if (service.isEventingAvailable()) {
            eventSubscriptionMonitor.monitorService(serviceUuid);
            /**
             * 目前的cancel不是立即能停掉，重启服务后，短时间内会同时有两个discovery线程，
             */
            scheduledDiscoveryManager.cancelDiscovery(serviceUuid);
            scheduledDiscoveryManager.scheduleDiscovery(serviceUuid);
        } else {
            scheduledDiscoveryManager.scheduleDiscovery(serviceUuid);
        }
//        beanManager.fireEvent(externalServiceMonitoringStartedEvent(serviceUuid));
        ApplicationContext context = AppContext.context();
        context.publishEvent(externalServiceMonitoringStartedEvent(this, serviceUuid));
    }

    /**
     * LockType.WRITE used due to concurrent access to discovery manager and subscription monitor.
     */
//    @Lock(WRITE)
//    @Transactional(SUPPORTS)
//    @AccessTimeout(value = 5, unit = SECONDS)
    @Transactional(propagation = Propagation.SUPPORTS, timeout = 5)
    public void stopMonitoringOfService(UUID serviceUuid) {
        eventSubscriptionMonitor.cancelMonitoring(serviceUuid);
        scheduledDiscoveryManager.cancelDiscovery(serviceUuid);
//        beanManager.fireEvent(externalServiceMonitoringStoppedEvent(serviceUuid));
        AppContext.context().publishEvent(externalServiceMonitoringStoppedEvent(this, serviceUuid));
    }

    
}
