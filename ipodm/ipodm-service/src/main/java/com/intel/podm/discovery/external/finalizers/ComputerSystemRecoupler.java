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


import static com.intel.podm.business.entities.redfish.base.StatusControl.statusOf;
import static com.intel.podm.common.utils.Contracts.requiresNonNull;
import static com.intel.podm.common.utils.IterableHelper.singleOrNull;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.ComputerSystem;

//@Dependent
@Component
class ComputerSystemRecoupler extends Recoupler<ComputerSystem> {
	private static final Logger logger = LoggerFactory.getLogger(ComputerSystemRecoupler.class);

    @Override
    protected void reattach(ComposedNode node, Collection<ComputerSystem> assets) {
        UUID associatedComputerSystemUuid = node.getAssociatedComputerSystemUuid();
        UUID associatedComputeServiceUuid = node.getAssociatedComputeServiceUuid();

        requiresNonNull(associatedComputerSystemUuid, "computerSystemUuid");
        requiresNonNull(associatedComputeServiceUuid, "serviceUuid");

        try {
            reattachComputerSystem(node, assets, associatedComputerSystemUuid, associatedComputeServiceUuid);
        } catch (IllegalStateException e) {
            logger.error(format("There is more than one computer system with UUID: %s on service with UUID: %s",
                associatedComputerSystemUuid,
                associatedComputeServiceUuid));
        }
    }

    @Override
    protected boolean verify(ComposedNode node) {
        ComputerSystem computerSystem = node.getComputerSystem();
        return computerSystem != null && statusOf(computerSystem).isEnabled().isHealthy().verify();
    }

    private void reattachComputerSystem(ComposedNode composedNode, Collection<ComputerSystem> systems,
                                        UUID associatedComputerSystemUuid, UUID associatedComputeServiceUuid) {

        ComputerSystem associatedComputerSystem = singleOrNull(systems.stream()
            .filter(computerSystem -> computerSystem.getService().getUuid() != null)
            .filter(computerSystem -> statusOf(computerSystem).isEnabled().isHealthy().verify())
            .filter(computerSystem -> Objects.equals(computerSystem.getService().getUuid(), associatedComputeServiceUuid))
            .filter(computerSystem -> computerSystem.getUuid() != null)
            .filter(computerSystem -> Objects.equals(computerSystem.getUuid(), associatedComputerSystemUuid))
            .collect(toList()));

        if (associatedComputerSystem != null) {
            associatedComputerSystem.getMetadata().setAllocated(true);
            composedNode.setComputerSystem(associatedComputerSystem);
        }
    }
}
