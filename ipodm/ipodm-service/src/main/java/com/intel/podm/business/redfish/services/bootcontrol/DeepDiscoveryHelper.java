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

import com.intel.podm.business.entities.EntityNotFoundException;
import com.intel.podm.business.entities.dao.ComputerSystemDao;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.common.types.Id;
import com.intel.podm.config.base.Config;
import com.intel.podm.config.base.ConfigProvider;
import com.intel.podm.config.base.Holder;
import com.intel.podm.config.base.dto.DiscoveryConfig;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.intel.podm.business.entities.redfish.base.StatusControl.statusOf;
import static com.intel.podm.common.types.DeepDiscoveryState.RUNNING;
import static com.intel.podm.common.types.DeepDiscoveryState.SCHEDULED_MANUALLY;
import static com.intel.podm.common.types.DeepDiscoveryState.WAITING_TO_START;
import static com.intel.podm.common.types.DiscoveryState.DEEP_IN_PROGRESS;
import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

import javax.annotation.Resource;

//@Dependent
@Component
class DeepDiscoveryHelper {
//    @Inject
//    @Config
//    private Holder<DiscoveryConfig> config;
	@Config
	@Resource(name="podmConfigProvider")
	private ConfigProvider config;

//    @Inject
	@Autowired
    private ComputerSystemDao computerSystemDao;

//    @Transactional(REQUIRES_NEW)
	@Transactional
    public void triggerDeepDiscovery(Id computerSystemId) throws EntityNotFoundException, DeepDiscoveryException {
        if (!config.get(DiscoveryConfig.class).isDeepDiscoveryEnabled()) {
            throw new DeepDiscoveryException("Deep discovery action cannot be triggered. Functionality has been disabled.");
        }

        ComputerSystem computerSystem = computerSystemDao.find(computerSystemId);
        if (!statusOf(computerSystem).isEnabled().isHealthy().verify()) {
            throw new DeepDiscoveryException("ComputerSystem should be enabled and healthy in order to invoke actions on it.");
        } else if (computerSystem.getComposedNode() != null) {
            throw new DeepDiscoveryException("Deep discovery action cannot be triggered. ComputerSystem is used by a Node.");
        } else if (isDeepDiscoveryInProgress(computerSystem)) {
            throw new DeepDiscoveryException("Deep discovery action cannot be triggered. Deep discovery is already in progress.");
        }

        computerSystem.setDiscoveryState(DEEP_IN_PROGRESS);
        computerSystem.getMetadata().setAllocated(true);
        computerSystem.getMetadata().setDeepDiscoveryState(SCHEDULED_MANUALLY);
    }

    private boolean isDeepDiscoveryInProgress(ComputerSystem computerSystem) {
        return computerSystem.getMetadata().isInAnyOfStates(SCHEDULED_MANUALLY, WAITING_TO_START, RUNNING);
    }
}
