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

package com.intel.podm.common.synchronization;

import static com.intel.podm.common.enterprise.utils.beans.JndiNames.SYNCHRONIZED_TASK_EXECUTOR;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.MetricRegistry;
import com.intel.podm.common.synchronization.monitoring.MetricRegistryFactory;

//@Singleton
@Component
@Lazy
public class SerialExecutorRegistry {
    private static final Logger log = LoggerFactory.getLogger(SerialExecutorRegistry.class);

//    @Inject
//    @Named(SYNCHRONIZED_TASK_EXECUTOR)
//    @Resource(name = SYNCHRONIZED_TASK_EXECUTOR)
    private ExecutorService executorService;

    @Autowired
    private MetricRegistryFactory metricRegistryFactory;

    private MetricRegistry metricRegistry;

    private HashMap<Object, SerialExecutor> registry = new HashMap<>();

    @PostConstruct
    private void init() {
        this.metricRegistry = metricRegistryFactory.getOrRegisterNew("PodM.Synchronization");
    }

    /**
     * LockType.WRITE used due to concurrent access to registry map that modifies it (put operation).
     */
//    @Lock(WRITE)
//    @Transactional(SUPPORTS)
//    @AccessTimeout(value = 5, unit = SECONDS)
    @Transactional(timeout = 5)
    public SerialExecutor getExecutor(Object key) {
        if (!registry.containsKey(key)) {
            log.info("Creating serial executorService for {}", key);
            registry.put(key, createExecutor(key));
        }
        return registry.get(key);
    }

    /**
     * LockType.WRITE used due to concurrent access to registry map that modifies it (remove operation).
     */
//    @Lock(WRITE)
//    @Transactional(SUPPORTS)
//    @AccessTimeout(value = 5, unit = SECONDS)
    @Transactional(timeout = 5)
    public void unregisterExecutor(Object synchronizationKey) {
        registry.remove(synchronizationKey);
    }

    private SerialExecutor createExecutor(Object key) {
        return new InstrumentedSerialExecutor(
            new SerialExecutorImpl(key, executorService),
            metricRegistry
        );
    }
}
