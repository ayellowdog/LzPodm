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

package com.inspur.podm.common.intel.types.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

import static com.inspur.podm.common.intel.utils.Contracts.checkState;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@JacksonAnnotation
public @interface AsUnassigned {
    Strategy[] value();

    enum Strategy {
        WHEN_NULL(Objects::isNull),
        WHEN_EMPTY_COLLECTION(object -> {
            if (object == null) {
                return false;
            }

            checkState(
                Collection.class.isAssignableFrom(object.getClass()),
                "AsUnassigned::WHEN_EMPTY_COLLECTION can be assigned only for Collection descendants."
            );

            return ((Collection<?>) object).isEmpty();
        });

        private final Function<Object, Boolean> strategy;

        Strategy(Function<Object, Boolean> strategy) {
            this.strategy = strategy;
        }

        public Boolean isUnassigned(Object object) {
            return strategy.apply(object);
        }
    }
}