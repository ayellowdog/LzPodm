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

package com.intel.podm.business.redfish.services.allocation.strategy;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.redfish.services.assembly.tasks.ChangeTpmStateTask;
import com.intel.podm.common.enterprise.utils.beans.BeanFactory;
import com.intel.podm.common.types.actions.ChangeTpmStatusUpdateDefinition;

@Component
public class ChangeTpmStateTaskFactory {
    @Autowired
    private BeanFactory beanFactory;

    @Transactional(propagation = Propagation.SUPPORTS)
    public ChangeTpmStateTask createChangeTpmStateTask(ComputerSystem computerSystem,
                                                       ChangeTpmStatusUpdateDefinition changeTpmStatusUpdateDefinition) {
        ChangeTpmStateTask changeTpmStateTask = beanFactory.create(ChangeTpmStateTask.class).init(computerSystem.getId());
        changeTpmStateTask.setChangeTpmStatusUpdateDefinition(changeTpmStatusUpdateDefinition);
        return changeTpmStateTask;
    }
}
