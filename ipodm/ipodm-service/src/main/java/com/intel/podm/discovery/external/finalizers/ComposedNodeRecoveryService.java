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

import static com.intel.podm.common.types.ComposedNodeState.ASSEMBLED;
import static com.intel.podm.common.types.Health.OK;
import static com.intel.podm.common.types.State.ENABLED;
import static com.intel.podm.common.utils.Collections.filterByType;
import static java.lang.String.format;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.intel.podm.business.entities.dao.ComposedNodeDao;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.Drive;
import com.intel.podm.business.entities.redfish.Endpoint;
import com.intel.podm.business.entities.redfish.Volume;
import com.intel.podm.business.entities.redfish.base.ComposableAsset;
import com.intel.podm.common.types.ComposedNodeState;
import com.intel.podm.common.types.Status;

//@Dependent
@Component
public class ComposedNodeRecoveryService {
	private static final Logger logger = LoggerFactory.getLogger(ComposedNodeRecoveryService.class);
    @Autowired
    private ComposedNodeDao composedNodeDao;

    @Autowired
    private Recoupler<Drive> driveRecoupler;

    @Autowired
    private Recoupler<Endpoint> endpointRecoupler;

    @Autowired
    private Recoupler<Volume> volumeRecoupler;

    @Autowired
    private Recoupler<ComputerSystem> computerSystemRecoupler;

//    @Transactional(MANDATORY)
    @org.springframework.transaction.annotation.Transactional
    void recoverFromAssets(Set<ComposableAsset> assetsAvailableToAttach) {
        Collection<Drive> drives = filterByType(assetsAvailableToAttach, Drive.class);
        Collection<ComputerSystem> systems = filterByType(assetsAvailableToAttach, ComputerSystem.class);
        Collection<Endpoint> endpoints = filterByType(assetsAvailableToAttach, Endpoint.class);
        Collection<Volume> volumes = filterByType(assetsAvailableToAttach, Volume.class);

        for (ComposedNode node : composedNodeDao.getComposedNodesEligibleForRecovery()) {
            if (driveRecoupler.recouple(node, drives)
                && computerSystemRecoupler.recouple(node, systems)
                && endpointRecoupler.recouple(node, endpoints)
                && volumeRecoupler.recouple(node, volumes)) {
                recoverComposedNode(node);
            }
        }
    }

    private void recoverComposedNode(ComposedNode node) {
        ComposedNodeState composedNodeState = ASSEMBLED;
        Status composedNodeStatus = new Status(ENABLED, OK, OK);

        node.setEligibleForRecovery(false);
        node.setStatus(composedNodeStatus);

        node.setComposedNodeState(composedNodeState);
        logger.info(format("Putting composed Node: %s into %s state and %s status",
            node.getId(),
            composedNodeState,
            composedNodeStatus));
    }
}
