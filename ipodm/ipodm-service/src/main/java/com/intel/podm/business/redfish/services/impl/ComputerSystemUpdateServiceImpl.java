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

package com.intel.podm.business.redfish.services.impl;

import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.UpdateService;
import com.intel.podm.business.redfish.services.ServiceTraverser;
import com.intel.podm.common.synchronization.TaskCoordinator;
import com.intel.podm.common.types.redfish.RedfishComputerSystem;


@Service("ComputerSystem")
class ComputerSystemUpdateServiceImpl implements UpdateService<RedfishComputerSystem> {
    @Autowired
    private ComputerSystemUpdater updateService;

    @Autowired
    private TaskCoordinator taskCoordinator;

    @Autowired
    private ServiceTraverser traverser;

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public void perform(Context target, RedfishComputerSystem representation) throws BusinessApiException, TimeoutException {
        taskCoordinator.run(traverser.traverseServiceUuid(target),
            () -> updateService.updateComputerSystem(target, representation));
    }
}
