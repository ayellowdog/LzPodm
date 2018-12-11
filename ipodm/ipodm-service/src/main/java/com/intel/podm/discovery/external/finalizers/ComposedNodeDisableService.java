/*
 * Copyright (c) 2018 Intel Corporation
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

package com.intel.podm.discovery.external.finalizers;


import static com.intel.podm.business.entities.redfish.ComposedNode.OFFLINE_CRITICAL_STATUS;
import static com.intel.podm.common.types.ComposedNodeState.ASSEMBLED;
import static com.intel.podm.common.types.ComposedNodeState.FAILED;
import static java.lang.String.format;
import static javax.transaction.Transactional.TxType.MANDATORY;

import java.util.Objects;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.base.ComposableAsset;

//@Dependent
@Component
@Lazy
public class ComposedNodeDisableService {
	@Autowired
    private static final Logger logger = LoggerFactory.getLogger(ComposedNodeDisableService.class);

//    @Transactional(MANDATORY)
    @org.springframework.transaction.annotation.Transactional
    public void disableComposedNode(ComposedNode composedNode) {
        if (composedNode.isInAnyOfStates(ASSEMBLED)) {
            composedNode.setEligibleForRecovery(true);
        }

        composedNode.setComposedNodeState(FAILED);
        composedNode.setStatus(OFFLINE_CRITICAL_STATUS);
        logger.info(format("Putting composed Node: %s into %s state and %s status",
            composedNode.getId(),
            FAILED,
            OFFLINE_CRITICAL_STATUS
        ));
    }

    @Transactional(MANDATORY)
    public void disableFromAssets(Set<ComposableAsset> degradedAssets) {
        degradedAssets.stream()
            .peek(asset -> logger.trace("Degraded asset: " + asset))
            .map(ComposableAsset::getComposedNode)
            .filter(Objects::nonNull)
            .forEach(this::disableComposedNode);
    }
}
