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

package com.intel.podm.business.redfish.services.detach;

import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.services.context.Context;
import com.intel.podm.business.redfish.services.ServiceTraverser;
import com.intel.podm.business.redfish.services.VolumeActionsService;
import com.intel.podm.common.synchronization.TaskCoordinator;

@Component
public class VolumeDetachStrategy implements DetachResourceStrategy {
    @Autowired
    private TaskCoordinator taskCoordinator;

    @Autowired
    private ServiceTraverser traverser;

    @Autowired
    private VolumeActionsService volumeActionsService;

    @Override
    public void detach(Context composedNodeContext, Context resourceToAttach) throws BusinessApiException, TimeoutException {
        taskCoordinator.run(traverser.traverseServiceUuid(resourceToAttach), () -> volumeActionsService.detachVolume(composedNodeContext, resourceToAttach));
    }
}
