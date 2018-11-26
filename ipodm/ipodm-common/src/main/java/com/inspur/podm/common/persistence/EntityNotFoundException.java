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

package com.inspur.podm.common.persistence;

import static java.lang.String.format;

import com.inspur.podm.common.intel.types.Id;
import com.inspur.podm.common.persistence.base.Entity;

public final class EntityNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -2626828968591303554L;

    public <T extends Entity> EntityNotFoundException(Class<T> entityClass, Id id) {
        super(format("Entity not found, class: %s, id: %s.", entityClass.getSimpleName(), id));
    }
}
