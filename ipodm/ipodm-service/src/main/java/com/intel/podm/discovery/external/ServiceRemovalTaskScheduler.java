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

import com.intel.podm.common.logger.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.inject.Inject;

import static java.util.concurrent.TimeUnit.SECONDS;

@Startup
@Singleton
@DependsOn({"DiscoveryStartup"})
public class ServiceRemovalTaskScheduler {
    private static final Long TASK_DELAY_SECONDS = 10L;

    @Inject
    private Logger logger;

    @Inject
    private ServiceRemovalTask task;

    @Resource
    private ManagedScheduledExecutorService managedExecutorService;

    @PostConstruct
    private void schedule() {
        logger.d("Scheduling Service Removal Task...");
        managedExecutorService.scheduleWithFixedDelay(
            task,
            TASK_DELAY_SECONDS,
            TASK_DELAY_SECONDS,
            SECONDS
        );
    }
}
