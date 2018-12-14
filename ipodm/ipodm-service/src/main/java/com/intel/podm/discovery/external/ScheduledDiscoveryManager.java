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

package com.intel.podm.discovery.external;

import com.intel.podm.common.enterprise.utils.beans.BeanFactory;
import com.intel.podm.common.synchronization.TaskCoordinator;
import com.intel.podm.config.base.Config;
import com.intel.podm.config.base.ConfigProvider;
import com.intel.podm.config.base.Holder;
import com.intel.podm.config.base.dto.DiscoveryConfig;

import javax.annotation.Resource;
import javax.ejb.AccessTimeout;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static com.intel.podm.common.enterprise.utils.beans.JndiNames.SYNCHRONIZED_TASK_EXECUTOR;
import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.ejb.LockType.WRITE;
import static javax.transaction.Transactional.TxType.SUPPORTS;

//@Singleton
@Component
@Lazy
public class ScheduledDiscoveryManager {
    private Map<UUID, ScheduledFuture<?>> discoveryTasks = new HashMap<>();
    private Map<UUID, DiscoveryRunner> discoveryRunners = new HashMap<>();

//    @Inject
//    @Named(SYNCHRONIZED_TASK_EXECUTOR)
    @Autowired
    @Resource(name=SYNCHRONIZED_TASK_EXECUTOR)
    private ScheduledExecutorService discoveryTaskExecutor;

    @Autowired
    private TaskCoordinator taskCoordinator;

//    @Inject
//    @Config
//    private Holder<DiscoveryConfig> discoveryConfigHolder;
    @Config
    @Resource(name="podmConfigProvider")
    private ConfigProvider discoveryConfigHolder;

    @Autowired
    private BeanFactory beanFactory;

    private static final Logger logger = LoggerFactory.getLogger(ScheduledDiscoveryManager.class);


    /**
     * LockType.WRITE used due to concurrent access to discovery tasks map that modifies it (put operation)
     */
//    @Lock(WRITE)
//    @Transactional(SUPPORTS)
//    @AccessTimeout(value = 5, unit = SECONDS)
    @Transactional(propagation = Propagation.SUPPORTS, timeout = 5)
    public void scheduleDiscovery(UUID serviceUuid) {
        if (!discoveryTasks.containsKey(serviceUuid)) {
            ScheduledFuture<?> discoveryTask = scheduleDiscoveryTask(serviceUuid);
            discoveryTasks.put(serviceUuid, discoveryTask);
        } else {
            logger.warn("Discovery is already scheduled for service {}", serviceUuid);
        }
    }

    /**
     * LockType.WRITE used due to concurrent access to discovery tasks map that modifies it (remove operation)
     */
//    @Lock(WRITE)
//    @Transactional(SUPPORTS)
//    @AccessTimeout(value = 5, unit = SECONDS)
    @Transactional(propagation = Propagation.SUPPORTS, timeout = 5)
    public void cancelDiscovery(UUID serviceUuid) {
        discoveryRunners.remove(serviceUuid);
        ScheduledFuture discoveryTask = discoveryTasks.remove(serviceUuid);
        if (discoveryTask != null) {
            logger.debug("Discovery cancelled for service {}", serviceUuid);
            discoveryTask.cancel(false);
        } else {
            logger.warn("Discovery was already cancelled for service {} or was not scheduled", serviceUuid);
        }
    }

    private ScheduledFuture<?> scheduleDiscoveryTask(UUID serviceUuid) {
        DiscoveryConfig discoveryConfig = discoveryConfigHolder.get(DiscoveryConfig.class);
        long discoveryIntervalSeconds = discoveryConfig.getDiscoveryIntervalSeconds();
        long discoveryDelaySeconds = 0;

        return discoveryTaskExecutor.scheduleWithFixedDelay(
            () -> enqueueDiscovery(serviceUuid),
            discoveryDelaySeconds,
            discoveryIntervalSeconds,
            SECONDS
        );
    }

    private void enqueueDiscovery(UUID serviceUuid) {
        DiscoveryRunner discoveryRunner = getDiscoveryRunner(serviceUuid);
        taskCoordinator.registerAsync(serviceUuid, discoveryRunner);
    }

    private DiscoveryRunner getDiscoveryRunner(UUID serviceUuid) {
        DiscoveryRunner discoveryRunner = discoveryRunners.get(serviceUuid);
        if (discoveryRunner == null) {
            discoveryRunner = beanFactory.create(DiscoveryRunner.class);
            discoveryRunner.setServiceUuid(serviceUuid);
            discoveryRunners.put(serviceUuid, discoveryRunner);
        }

        return discoveryRunner;
    }
}
