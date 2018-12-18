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

package com.intel.podm.business.redfish.services.allocation;

import javax.persistence.OptimisticLockException;
import javax.transaction.RollbackException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.dao.GenericDao;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.common.types.ComposedNodeState;
import com.intel.podm.common.types.Id;

@Component
public class ComposedNodeStateChanger {
    @Autowired
    private GenericDao genericDao;

    @Transactional(propagation = Propagation.REQUIRED)
    @Retryable(value= {RollbackException.class, OptimisticLockException.class},maxAttempts = 3,backoff = @Backoff(delay = 100l,multiplier = 1))
    public void change(Id nodeId, ComposedNodeState nodeState) {
        ComposedNode composedNode = genericDao.find(ComposedNode.class, nodeId);
        composedNode.setComposedNodeState(nodeState);
    }
}
