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

import com.codahale.metrics.annotation.Metered;
import com.intel.podm.config.base.Config;
import com.intel.podm.config.base.ConfigProvider;
import com.intel.podm.config.base.Holder;
import com.intel.podm.config.base.dto.TaskCoordinatorConfig;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import static com.intel.podm.common.enterprise.utils.beans.JndiNames.SYNCHRONIZED_TASK_EXECUTOR;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

//@ApplicationScoped
@Service
public class TaskCoordinatorImpl implements TaskCoordinator {
	private static final Logger logger = LoggerFactory.getLogger(TaskCoordinatorImpl.class);
    @Autowired
    private SerialExecutorRegistry executorsRegistry;

    @Autowired
//    @Config
//    private Holder<TaskCoordinatorConfig> configHolder;
    ConfigProvider configHolder;

//    @Inject
//    @Named(SYNCHRONIZED_TASK_EXECUTOR)
    @Resource(name = SYNCHRONIZED_TASK_EXECUTOR)
    private ScheduledExecutorService synchronizedTaskExecutor;

    @Override
    public void registerAsync(UUID synchronizationKey, Runnable task) {
        executorsRegistry.getExecutor(synchronizationKey).executeAsync(task);
    }

    @Override
    public <E extends Exception> void run(UUID synchronizationKey, ThrowingRunnable<E> task) throws TimeoutException, E {
        run(synchronizationKey, configHolder.get(TaskCoordinatorConfig.class).getMaxTimeToWaitForAsyncTaskSeconds(), task);
    }

    @Metered(name = "coordinated-synchronous-tasks")
    private <E extends Exception> void run(Object synchronizationKey, Duration timeToWaitFor, ThrowingRunnable<E> task) throws TimeoutException, E {
        executorsRegistry.getExecutor(synchronizationKey).executeSync(timeToWaitFor, task);
    }

    @Override
    public <E extends Exception, R> R call(UUID synchronizationKey, ThrowingCallable<R, E> task) throws TimeoutException, E {
        return run(synchronizationKey, configHolder.get(TaskCoordinatorConfig.class).getMaxTimeToWaitForAsyncTaskSeconds(), task);
    }

    @Override
//    @SuppressWarnings({"checkstyle:IllegalCatch"})
    public void scheduleWithFixedDelay(UUID synchronizationKey, Runnable task, Duration initialDelay, Duration delay) {
        schedule(synchronizationKey, () -> {
            try {
                task.run();
            } catch (Throwable throwable) {
                logger.error("Scheduled '" + task + "' task finished exceptionally. Will not be scheduled again.", throwable);
                throw throwable;
            }
            scheduleWithFixedDelay(synchronizationKey, task, delay, delay);
        }, initialDelay);
    }

    private <E extends Exception, R> R run(Object synchronizationKey, Duration timeToWaitFor, ThrowingCallable<R, E> task) throws TimeoutException, E {
        return executorsRegistry.getExecutor(synchronizationKey).executeSync(timeToWaitFor, task);
    }

    private void schedule(UUID synchronizationKey, Runnable task, Duration delay) {
        synchronizedTaskExecutor.schedule(() -> registerAsync(synchronizationKey, task), delay.toMillis(), MILLISECONDS);
    }
}
