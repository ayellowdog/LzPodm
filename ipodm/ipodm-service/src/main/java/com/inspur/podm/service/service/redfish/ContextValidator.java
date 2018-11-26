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

package com.inspur.podm.service.service.redfish;

import static com.inspur.podm.common.intel.utils.Contracts.requiresNonNull;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.context.ContextType;
import com.inspur.podm.common.intel.types.Id;
import com.inspur.podm.common.persistence.base.Entity;
import com.inspur.podm.service.dao.GenericDao;

/**
 * Allows to verify whether given {@link ContextType} is correct
 */
@Dependent
public class ContextValidator {
    @Inject
    GenericDao genericDao;

    @Inject
    ContextTypeToEntityMapper contextTypeToEntityMapper;

    public boolean isValid(Context context) {
        requiresNonNull(context, "context");

        Entity entity = getIfValid(context);
        return entity != null;
    }

    private Entity getIfValid(Context context) {
        Entity entity = tryGet(context.getType(), context.getId());

        if (entity == null || context.getParent() == null) {
            return entity;
        }

        Entity parent = getIfValid(context.getParent());
        return parent != null && entity.containedBy(parent)
            ? entity
            : null;
    }

    private Entity tryGet(ContextType type, Id id) {
        Class<? extends Entity> entityClass;
        try {
            entityClass = contextTypeToEntityMapper.getEntityClass(type);
        } catch (UnsupportedOperationException e) {
            return null;
        }
        return genericDao.tryFind(entityClass, id).orElse(null);
    }
}
