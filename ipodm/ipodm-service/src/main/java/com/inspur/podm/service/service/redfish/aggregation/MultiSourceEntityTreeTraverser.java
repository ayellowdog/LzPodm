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

package com.inspur.podm.service.service.redfish.aggregation;

import static com.inspur.podm.api.business.services.context.Context.hasParentOfTypeOnTopOf;
import static com.inspur.podm.api.business.services.context.ContextType.CHASSIS;
import static com.inspur.podm.api.business.services.context.ContextType.COMPUTER_SYSTEM;
import static java.util.EnumSet.of;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.inspur.podm.api.business.ContextResolvingException;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.context.ContextType;
import com.inspur.podm.common.persistence.base.Entity;
import com.inspur.podm.common.persistence.entity.Chassis;
import com.inspur.podm.common.persistence.entity.ComputerSystem;
import com.inspur.podm.common.persistence.entity.DiscoverableEntity;
import com.inspur.podm.service.service.redfish.ContextTypeToEntityMapper;
import com.inspur.podm.service.service.redfish.EntityTreeTraverser;

@Dependent
@SuppressWarnings({"checkstyle:ClassFanOutComplexity"})
public class MultiSourceEntityTreeTraverser {
    private static final EnumSet<ContextType> MULTI_SOURCE_ROOT_TYPES = of(COMPUTER_SYSTEM, CHASSIS);

    @Inject
    private EntityTreeTraverser traverser;

    @Inject
    private ComputerSystemSubResourcesFinder computerSystemSubResourcesFinder;

    @Inject
    private ChassisSubResourcesFinder chassisSubResourcesFinder;

    @Inject
    private ContextTypeToEntityMapper contextMapper;

    public Entity traverse(Context context) throws ContextResolvingException {
        if (!isMultiSourceResource(context)) {
            return traverser.traverse(context);
        } else {
            Entity multiSourceRoot = getMultiSourceRoot(context);

            Collection<DiscoverableEntity> entities = getSubResourcesAsDiscoverableEntities(context, multiSourceRoot);
            for (DiscoverableEntity entity : entities) {
                if (Objects.equals(entity.getId(), context.getId())) {
                    return entity;
                }
            }

            throw new ContextResolvingException(context);
        }
    }

    private Collection<DiscoverableEntity> getSubResourcesAsDiscoverableEntities(Context context, Entity multiSourceRoot) throws ContextResolvingException {
        Collection<DiscoverableEntity> entities = new ArrayList<>();
        if (DiscoverableEntity.class.isInstance(multiSourceRoot)) {
            entities.add((DiscoverableEntity) multiSourceRoot);
        }

        Class<? extends DiscoverableEntity> entityClass = getTargetContextClass(context);
        ContextType contextType = contextMapper.tryGetContextType(multiSourceRoot.getClass());
        switch (contextType) {
            case COMPUTER_SYSTEM:
                entities.addAll(computerSystemSubResourcesFinder.getUniqueSubResourcesOfClass((ComputerSystem) multiSourceRoot, entityClass));
                break;
            case CHASSIS:
                entities.addAll(chassisSubResourcesFinder.getUniqueSubResourcesOfClass((Chassis) multiSourceRoot, entityClass));
                break;
            default:
                break;
        }
        return entities;
    }

    @SuppressWarnings("unchecked")
    private Class<? extends DiscoverableEntity> getTargetContextClass(Context context) throws ContextResolvingException {
        Class<? extends Entity> entityClass = contextMapper.getEntityClass(context.getType());
        if (!DiscoverableEntity.class.isAssignableFrom(entityClass)) {
            throw new ContextResolvingException(context);
        }
        return (Class<? extends DiscoverableEntity>) entityClass;
    }

    private Entity getMultiSourceRoot(Context context) throws ContextResolvingException {
        Context checkedContext = context;
        while (checkedContext != null) {
            if (MULTI_SOURCE_ROOT_TYPES.contains(checkedContext.getType())) {
                return traverser.traverse(checkedContext);
            } else {
                checkedContext = checkedContext.getParent();
            }
        }
        throw new ContextResolvingException("Multi-Source Root Entity was not found for specified context.", context, null);
    }

    private boolean isMultiSourceResource(Context context) {
        return MULTI_SOURCE_ROOT_TYPES.stream()
            .anyMatch(multiSourceType -> Objects.equals(multiSourceType, context.getType()) || hasParentOfTypeOnTopOf(context, multiSourceType));
    }
}
