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

package com.intel.podm.business.entities.dao;

import static com.intel.podm.business.entities.redfish.EthernetSwitchPort.GET_ETHERNET_SWITCH_PORT_BY_NEIGHBOR_MAC;
import static com.intel.podm.business.entities.redfish.base.StatusControl.statusOf;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.NonUniqueResultException;
import com.intel.podm.business.entities.redfish.EthernetSwitchPort;
import com.intel.podm.common.types.Id;
import com.intel.podm.common.types.net.MacAddress;

//@ApplicationScoped
@Component
public class EthernetSwitchPortDao extends Dao<EthernetSwitchPort> {
	@Transactional(propagation = Propagation.MANDATORY)
    public EthernetSwitchPort getOrThrow(Id portId) {
        Optional<EthernetSwitchPort> expectedSwitchPort = tryFind(portId);
        if (!expectedSwitchPort.isPresent()) {
            throw new IllegalStateException("Target Ethernet Switch Port was not found!");
        }
        return expectedSwitchPort.get();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public EthernetSwitchPort getEnabledAndHealthyEthernetSwitchPortByNeighborMac(MacAddress neighborMac) throws NonUniqueResultException {
        if (neighborMac == null) {
            return null;
        }

        TypedQuery<EthernetSwitchPort> query = entityManager.createNamedQuery(GET_ETHERNET_SWITCH_PORT_BY_NEIGHBOR_MAC, EthernetSwitchPort.class);
        query.setParameter("neighborMac", neighborMac);

        List<EthernetSwitchPort> foundPorts = query.getResultList().stream()
            .filter(port -> statusOf(port).isEnabled().isHealthy().verify())
            .collect(toList());

        int foundPortsSize = foundPorts.size();
        if (foundPortsSize > 1) {
            throw new NonUniqueResultException(
                format("Couldn't find single, enabled and healthy Ethernet Switch Port with neighbor MAC Address: '%s'. Found %d Ethernet Switch Ports.",
                    neighborMac, foundPortsSize));
        }

        return !foundPorts.isEmpty() ? foundPorts.get(0) : null;
    }
}
