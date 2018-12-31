/*
 * Copyright (c) 2016-2018 Intel Corporation
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

package com.intel.podm.discovery.external.event;

import static com.intel.podm.common.enterprise.utils.beans.JndiNames.EVENT_SUBSCRIPTION_TASK_EXECUTOR;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.config.base.Config;
import com.intel.podm.config.base.ConfigProvider;
import com.intel.podm.config.base.Holder;
import com.intel.podm.config.base.dto.EventsConfig;

//@Singleton
@Component
@Lazy
public class EventSubscriptionMonitor {
    private Map<UUID, ScheduledFuture<?>> eventSubscriptionTasks = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(EventSubscriptionMonitor.class);

//    @Autowired
//    @Config
//    private Holder<EventsConfig> eventsConfig;
    @Config
    @Resource(name="podmConfigProvider")
    private ConfigProvider eventsConfig;

//    @Inject
//    @Named(EVENT_SUBSCRIPTION_TASK_EXECUTOR)
    @Resource(name = EVENT_SUBSCRIPTION_TASK_EXECUTOR)
    private ScheduledExecutorService eventSubscriptionTaskExecutor;

    @Autowired
    private EventSubscriptionTaskFactory eventSubscriptionTaskFactory;

    /**
     * LockType.WRITE used due to concurrent access to event subscription tasks map that modifies it (put operation).
     */
//    @Lock(WRITE)
//    @Transactional(SUPPORTS)
//    @AccessTimeout(value = 5, unit = SECONDS)
    @Transactional(propagation = Propagation.SUPPORTS, timeout = 5)
    public synchronized void monitorService(UUID serviceUuid) {
        if (!eventSubscriptionTasks.containsKey(serviceUuid)) {
            EventSubscriptionTask subscriptionTask = eventSubscriptionTaskFactory.create(serviceUuid);
            // run synchronously for the first time
            subscriptionTask.run();

            ScheduledFuture<?> eventSubscriptionTask = scheduleEventSubscriptionTask(subscriptionTask);
            eventSubscriptionTasks.put(serviceUuid, eventSubscriptionTask);
        } else {
            logger.warn("Event subscription monitoring is already active for service {}", serviceUuid);
        }
    }

    /**
     * LockType.WRITE used due to concurrent access to event subscription tasks map that modifies it (remove operation).
     */
//    @Lock(WRITE)
//    @Transactional(SUPPORTS)
//    @AccessTimeout(value = 5, unit = SECONDS)
    @Transactional(propagation = Propagation.SUPPORTS, timeout = 5)
    public synchronized void cancelMonitoring(UUID serviceUuid) {
        ScheduledFuture<?> monitoringTask = eventSubscriptionTasks.remove(serviceUuid);
        if (monitoringTask != null) {
            monitoringTask.cancel(false);
        }
    }

    private ScheduledFuture<?> scheduleEventSubscriptionTask(EventSubscriptionTask subscriptionTask) {
        long eventSubscriptionIntervalSeconds = eventsConfig.get(EventsConfig.class).getEventSubscriptionIntervalSeconds();
        return eventSubscriptionTaskExecutor.scheduleWithFixedDelay(
            subscriptionTask,
            eventSubscriptionIntervalSeconds,
            eventSubscriptionIntervalSeconds,
            SECONDS
        );
    }
}
