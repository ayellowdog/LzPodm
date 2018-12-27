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

package com.intel.podm.business.redfish.services.helpers;

import static com.intel.podm.common.types.ComposedNodeState.ASSEMBLED;
import static com.intel.podm.common.types.ComposedNodeState.FAILED;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.ResourceStateMismatchException;
import com.inspur.podm.api.business.services.context.Context;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.redfish.services.EntityTreeTraverser;

@Component
public class NodeActionsValidator {
    @Autowired
    private EntityTreeTraverser traverser;

    @Transactional(propagation = Propagation.REQUIRED)
    public void validateIfActionCanBePerformedOnNode(Context composedNodeContext) throws BusinessApiException {
        ComposedNode composedNode = (ComposedNode) traverser.traverse(composedNodeContext);
        validateIfActionCanBePerformedOnNode(composedNode);
    }


    @Transactional(propagation = Propagation.MANDATORY)
    public void validateIfActionCanBePerformedOnNode(ComposedNode composedNode) throws ResourceStateMismatchException {
        if (composedNode.getComputerSystem() == null) {
            throw new ResourceStateMismatchException("There is no Computer System related with requested Node.");
        }

        if (!composedNode.isInAnyOfStates(ASSEMBLED, FAILED)) {
            throw new ResourceStateMismatchException("Composed Node should be assembled in order to invoke this action on it.");
        }
    }
}
