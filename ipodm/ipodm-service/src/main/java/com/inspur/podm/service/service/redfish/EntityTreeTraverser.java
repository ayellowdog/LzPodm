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

import com.inspur.podm.api.business.ContextResolvingException;
import com.inspur.podm.common.persistence.EntityNotFoundException;
import com.inspur.podm.service.dao.GenericDao;
import com.inspur.podm.common.persistence.base.Entity;
import com.inspur.podm.api.business.services.context.Context;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.inspur.podm.common.intel.utils.Contracts.requiresNonNull;
import static java.util.stream.Collectors.toSet;
import static javax.transaction.Transactional.TxType.MANDATORY;


/**
 * Allows to traverse path (expressed by {@link Context}) of {@link Entity} tree.
 * Throws exception if {@link Entity} is unreachable.
 */
@Dependent
public class EntityTreeTraverser {
    @Inject
    GenericDao genericDao;

    @Inject
    ContextValidator validator;

    @Inject
    ContextTypeToEntityMapper mapper;

    @Transactional(MANDATORY)
    public Optional<Entity> tryTraverse(Context context) {
        requiresNonNull(context, "context");

        if (!validator.isValid(context)) {
            return Optional.empty();
        }

        Class<? extends Entity> entityClass = mapper.getEntityClass(context.getType());
        return Optional.of(genericDao.find(entityClass, context.getId()));
    }

    @Transactional(MANDATORY)
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

    @Transactional(MANDATORY)
    @SuppressWarnings({"unchecked"})
    public <T extends Entity> Set<Optional<T>> tryTraverse(Set<Context> contexts) {
        requiresNonNull(contexts, "contexts");

        return contexts.stream()
            .map(context -> (Optional<T>) tryTraverse(context))
            .collect(toSet());
    }

    @Transactional(MANDATORY)
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
