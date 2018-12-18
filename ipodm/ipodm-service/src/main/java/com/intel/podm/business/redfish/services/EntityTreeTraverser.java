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

package com.intel.podm.business.redfish.services;

import static com.intel.podm.common.utils.Contracts.requiresNonNull;
import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.ContextResolvingException;
import com.inspur.podm.api.business.services.context.Context;
import com.intel.podm.business.entities.EntityNotFoundException;
import com.intel.podm.business.entities.dao.GenericDao;
import com.intel.podm.business.entities.redfish.base.Entity;


/**
 * Allows to traverse path (expressed by {@link Context}) of {@link Entity} tree.
 * Throws exception if {@link Entity} is unreachable.
 */
@Component
public class EntityTreeTraverser {
    @Autowired
    GenericDao genericDao;

    @Autowired
    ContextValidator validator;

    @Autowired
    ContextTypeToEntityMapper mapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<Entity> tryTraverse(Context context) {
        requiresNonNull(context, "context");

        if (!validator.isValid(context)) {
            return Optional.empty();
        }

        Class<? extends Entity> entityClass = mapper.getEntityClass(context.getType());
        return Optional.of(genericDao.find(entityClass, context.getId()));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Entity traverse(Context context) throws ContextResolvingException {
        requiresNonNull(context, "context");

        if (!validator.isValid(context)) {
            throw new ContextResolvingException(context);
        }

        try {
            Class<? extends Entity> entityClass = mapper.getEntityClass(context.getType());
            return genericDao.find(entityClass, context.getId());
        } catch (EntityNotFoundException | UnsupportedOperationException e) {
            throw new ContextResolvingException(e.getMessage(), context, e);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @SuppressWarnings({"unchecked"})
    public <T extends Entity> Set<Optional<T>> tryTraverse(Set<Context> contexts) {
        requiresNonNull(contexts, "contexts");

        return contexts.stream()
            .map(context -> (Optional<T>) tryTraverse(context))
            .collect(toSet());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @SuppressWarnings({"unchecked"})
    public <T extends Entity> Set<T> traverse(Set<Context> contexts) throws ContextResolvingException {
        requiresNonNull(contexts, "contexts");

        Set<T> entities = new HashSet<>();

        for (Context context : contexts) {
            entities.add((T) traverse(context));
        }

        return entities;
    }
}
