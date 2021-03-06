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

package com.intel.podm.business.redfish.services.assembly.tasks;

import static com.intel.podm.common.utils.Contracts.requiresNonNull;
import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;

import com.intel.podm.common.synchronization.ThrowingRunnable;

class ChainElement<T extends ThrowingRunnable<RuntimeException>> implements Runnable {

    private T task;

    private Optional<ChainElement<T>> nextElement;

    private Consumer<ChainElement<T>> executor;

    private Optional<Consumer<RuntimeException>> exceptionHandler;

    ChainElement(Consumer<ChainElement<T>> executor,
                 LinkedList<T> tasks,
                 Consumer<RuntimeException> exceptionHandler) {

        requiresNonNull(tasks, "tasks");
        requiresNonNull(executor, "executor");
        this.task = tasks.poll();
        this.executor = executor;
        this.exceptionHandler = ofNullable(exceptionHandler);
        this.nextElement = tasks.isEmpty() ? empty() : of(new ChainElement<>(executor, tasks, exceptionHandler));
    }

    public T getTask() {
        return task;
    }

    @SuppressWarnings({"checkstyle:IllegalCatch"})
    @Override
    public void run() {
        try {
            task.run();
        } catch (RuntimeException e) {
            exceptionHandler.ifPresent(h -> h.accept(e));
            return;
        }
        nextElement.ifPresent(executor::accept);
    }

    public Void execute() {
        executor.accept(this);
        return null;
    }

    @Override
    public String toString() {
        return format("ChainElement(%s)", task.toString());
    }
}
