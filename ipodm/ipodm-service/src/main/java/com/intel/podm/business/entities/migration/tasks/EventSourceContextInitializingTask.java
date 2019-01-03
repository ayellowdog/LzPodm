/*
 * Copyright (c) 2017-2018 Intel Corporation
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

package com.intel.podm.business.entities.migration.tasks;

import com.intel.podm.business.entities.migration.JpaBasedDataMigrationTask;
import com.intel.podm.business.entities.observers.EventableEntityEventSourceContextInitializer;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.DiscoverableEntity;
import com.intel.podm.business.entities.redfish.StorageController;
import com.intel.podm.business.entities.redfish.base.Entity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

//@ApplicationScoped
public class EventSourceContextInitializingTask extends JpaBasedDataMigrationTask {

    @SuppressWarnings("unchecked")
	private static final List<Class<? extends Entity>> EVENTABLE_CLASSES = newArrayList(
        DiscoverableEntity.class,
        ComposedNode.class,
        StorageController.class
    );

    @Autowired
    private EventableEntityEventSourceContextInitializer eventableEntityEventSourceContextInitializer;

    @PersistenceContext
    private EntityManager entityManager;

//    @Transactional(REQUIRES_NEW)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void run() {
        for (Class<? extends Entity> eventableClass : EVENTABLE_CLASSES) {
            List<? extends Entity> entities = findAll(eventableClass);
            entities.forEach(entity -> eventableEntityEventSourceContextInitializer.onEntityAdded(entity));
        }
    }

    private <T extends Entity> List<T> findAll(Class<T> entityClass) {
        return entityManager.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass).getResultList();
    }
}
