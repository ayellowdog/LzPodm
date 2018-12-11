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

package com.intel.podm.common.enterprise.utils.tasks;


import static java.lang.String.format;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;

import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedTaskListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class LoggingTaskListener implements ManagedTaskListener {
	private static final Logger logger = LoggerFactory.getLogger(LoggingTaskListener.class);

    @Override
    public void taskSubmitted(Future<?> future, ManagedExecutorService executor, Object task) {
    }

    @Override
    public void taskAborted(Future<?> future, ManagedExecutorService executor, Object task, Throwable exception) {
        logger.trace(task + " was aborted due to {}", exception.toString());

        //According to Java docs, the exception exists when task was cancelled, skipped or aborted (failed to start)
        //We assume that task might be cancelled only by programmer and shouldn't be logged
        //otherwise we should log the cause of termination
        if (!(exception instanceof CancellationException)) {
            logger.error("Task failed to start due to exception.", exception.getCause());
        }
    }

    @Override
    public void taskDone(Future<?> future, ManagedExecutorService executor, Object task, Throwable exception) {
        //Since cancellation is within life cycle of task, we shouldn't log any information about it.
        if (future.isCancelled()) { // :(
            logger.trace("Task {} was cancelled", task);
        } else if (exception != null) {
            logger.error(format("Task %s has failed with exception", task), exception);
        }
    }

    @Override
    public void taskStarting(Future<?> future, ManagedExecutorService executor, Object task) {
    }
}
