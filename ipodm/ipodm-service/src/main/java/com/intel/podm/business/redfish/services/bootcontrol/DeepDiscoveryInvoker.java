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
import com.intel.podm.business.redfish.services.actions.ComputerSystemUpdateInvoker;
import com.intel.podm.business.redfish.services.actions.ResetActionInvoker;
import com.intel.podm.common.types.Id;
import com.intel.podm.common.types.actions.ComputerSystemUpdateDefinition;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static com.intel.podm.common.types.BootSourceMode.LEGACY;
import static com.intel.podm.common.types.BootSourceState.ONCE;
import static com.intel.podm.common.types.BootSourceType.PXE;
import static java.lang.String.format;
import static javax.transaction.Transactional.TxType.REQUIRED;

@Dependent
class DeepDiscoveryInvoker {
    @Inject
    private ComputerSystemUpdateInvoker computerSystemUpdateInvoker;

    @Inject
    private ResetActionInvoker resetActionInvoker;

    @Inject
    private ComputerSystemDao computerSystemDao;

    @Transactional(REQUIRED)
    public void startDeepDiscovery(Id computerSystemId) throws DeepDiscoveryException {
        ComputerSystem computerSystem = computerSystemDao.tryFind(computerSystemId).orElseThrow(() -> {
            String msg = format("Starting deep discovery failed for ComputerSystem %s: it has been removed during deep discovery", computerSystemId);
            return new DeepDiscoveryException(msg);
        });

        try {
            resetActionInvoker.powerOff(computerSystem);
            ComputerSystemUpdateDefinition computerSystemUpdateDefinition = new ComputerSystemUpdateDefinition(LEGACY, PXE, ONCE);
            computerSystemUpdateInvoker.updateComputerSystem(computerSystem, computerSystemUpdateDefinition);
            resetActionInvoker.powerOn(computerSystem);
        } catch (EntityOperationException e) {
            throw new DeepDiscoveryException(format("Starting deep discovery failed for ComputerSystem %s", computerSystem.getUuid()), e);
        }
    }
}
