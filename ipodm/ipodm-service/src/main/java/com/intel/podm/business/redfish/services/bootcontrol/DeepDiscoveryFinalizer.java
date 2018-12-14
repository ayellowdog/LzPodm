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

package com.intel.podm.business.redfish.services.bootcontrol;

import com.inspur.podm.api.business.EntityOperationException;
import com.intel.podm.business.entities.dao.ComputerSystemDao;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.redfish.services.actions.ResetActionInvoker;
//import com.intel.podm.common.enterprise.utils.retry.RetryOnRollback;
//import com.intel.podm.common.enterprise.utils.retry.RetryOnRollbackInterceptor;
import com.intel.podm.common.types.Id;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.OptimisticLockException;
import javax.transaction.RollbackException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.intel.podm.common.types.DeepDiscoveryState.DONE;
import static com.intel.podm.common.types.DeepDiscoveryState.FAILED;
import static com.intel.podm.common.types.DeepDiscoveryState.RUNNING;
import static com.intel.podm.common.types.DiscoveryState.DEEP;
import static com.intel.podm.common.types.DiscoveryState.DEEP_FAILED;
import static javax.transaction.Transactional.TxType.MANDATORY;
import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

/**
 * Class responsible for managing finished deep discovery
 */
//@Dependent
//@Interceptors(RetryOnRollbackInterceptor.class)
@Component
class DeepDiscoveryFinalizer {
	private static final Logger logger = LoggerFactory.getLogger(DeepDiscoveryFinalizer.class);

    @Autowired
    private ComputerSystemDao computerSystemDao;

    @Autowired
    private ResetActionInvoker resetActionInvoker;

//    @Transactional(MANDATORY)
    @Transactional(propagation = Propagation.MANDATORY)
    public void finalizeSuccessfulDeepDiscovery(Id computerSystemId) {
        Optional<ComputerSystem> computerSystemOption = computerSystemDao.tryFind(computerSystemId);

        if (computerSystemOption.isPresent()) {
            ComputerSystem computerSystem = computerSystemOption.get();
            deallocateComputerSystem(computerSystem);
            computerSystem.setDiscoveryState(DEEP);
        } else {
            logger.warn("ComputerSystem {} has been removed during deep discovery", computerSystemId);
        }
    }

//    @RetryOnRollback(3)
//    @Transactional(REQUIRES_NEW)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
	@Retryable(value= {RollbackException.class, OptimisticLockException.class},maxAttempts = 3,backoff = @Backoff(delay = 100l,multiplier = 1))
    public void finalizeDeepDiscoveryOnTimeout(Id computerSystemId, UUID taskUuid) {
        Optional<ComputerSystem> optionalComputerSystem = computerSystemDao.tryFind(computerSystemId);
        if (!optionalComputerSystem.isPresent()) {
            logger.warn("ComputerSystem {} has been removed during deep discovery", computerSystemId);
            return;
        }
        ComputerSystem computerSystem = optionalComputerSystem.get();

        if (Objects.equals(taskUuid, computerSystem.getMetadata().getTaskUuid()) && computerSystem.getMetadata().isInAnyOfStates(RUNNING)) {
            deallocateComputerSystem(computerSystem);
            computerSystem.setDiscoveryState(DEEP_FAILED);
            computerSystem.getMetadata().setDeepDiscoveryState(FAILED);
            logger.warn("Deep discovery timed out for ComputerSystem {}, [ service: {}, path: {} ]",
                computerSystem.getId(), computerSystem.getService().getBaseUri(), computerSystem.getSourceUri());
        }
    }

    private void deallocateComputerSystem(ComputerSystem computerSystem) {
        computerSystem.getMetadata().setAllocated(false);
        computerSystem.getMetadata().setDeepDiscoveryState(DONE);
        try {
            resetActionInvoker.shutdownGracefully(computerSystem);
        } catch (EntityOperationException e) {
            logger.error("Graceful shutdown failed for ComputerSystem {} , [ service: {}, path: {} ]",
                computerSystem.getId(), computerSystem.getService().getBaseUri(), computerSystem.getSourceUri());
        }
    }
}
