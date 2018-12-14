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

package com.intel.podm.business.redfish.services.actions;

import com.intel.podm.business.entities.dao.ComputerSystemDao;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.embeddables.Boot;
import com.intel.podm.client.resources.redfish.BootObject;
import com.intel.podm.client.resources.redfish.ComputerSystemResource;
//import com.intel.podm.common.enterprise.utils.retry.RetryOnRollback;
//import com.intel.podm.common.enterprise.utils.retry.RetryOnRollbackInterceptor;
import com.intel.podm.common.types.Id;
import com.intel.podm.mappers.subresources.TrustedModuleMapper;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.OptimisticLockException;
import javax.transaction.RollbackException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.intel.podm.common.utils.Contracts.requiresNonNull;
import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

//@Dependent
//@Interceptors(RetryOnRollbackInterceptor.class)
@Component
@Lazy
class ComputerSystemUpdater {
//    @Inject
	@Autowired
    private ComputerSystemDao computerSystemDao;

    @Inject
    private TrustedModuleMapper trustedModuleMapper;

    private static final Logger logger = LoggerFactory.getLogger(ComputerSystemUpdater.class);

//    @RetryOnRollback(3)
//    @Transactional(REQUIRES_NEW)
    @Transactional
    @Retryable(value= {RollbackException.class, OptimisticLockException.class},maxAttempts = 3,backoff = @Backoff(delay = 100l,multiplier = 1))
    public void updateComputerSystemWithRetry(Id computerSystemId, ComputerSystemResource computerSystemResource) throws IllegalStateException {
        Optional<ComputerSystem> expectedComputerSystem = computerSystemDao.tryFind(computerSystemId);
        if (!expectedComputerSystem.isPresent()) {
            String errorMessage = "ComputerSystem was removed before it could be updated";
            logger.info(errorMessage + ", expected ComputerSystem id: {}", computerSystemId);
            throw new IllegalStateException(errorMessage);
        }
        ComputerSystem computerSystem = expectedComputerSystem.get();
        requiresNonNull(computerSystem.getBoot(), "computerSystem.getBoot()");
        requiresNonNull(computerSystemResource, "computerSystemResource");
        requiresNonNull(computerSystemResource.getBootObject(), "computerSystemResource.getBoot()");

        Boot boot = computerSystem.getBoot();
        BootObject updatedBoot = computerSystemResource.getBootObject();
        updatedBoot.getBootSourceOverrideTarget().ifAssigned(boot::setBootSourceOverrideTarget);
        updatedBoot.getBootSourceOverrideEnabled().ifAssigned(boot::setBootSourceOverrideEnabled);
        updatedBoot.getBootSourceOverrideMode().ifAssigned(boot::setBootSourceOverrideMode);

        computerSystemResource.getAssetTag().ifAssigned(computerSystem::setAssetTag);

        updateSecurityAttributes(computerSystemResource, computerSystem);
    }

    private void updateSecurityAttributes(ComputerSystemResource computerSystemResource, ComputerSystem computerSystem) {
        computerSystemResource.getUserModeEnabled().ifAssigned(computerSystem::setUserModeEnabled);
        computerSystemResource.getTrustedModules().ifAssigned(trustedModules ->
            trustedModuleMapper.map(trustedModules, computerSystem.getTrustedModules(), computerSystem::addTrustedModule));
    }
}
