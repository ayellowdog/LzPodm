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

package com.inspur.podm.service.dao;

import com.inspur.podm.common.persistence.NonUniqueResultException;
import com.inspur.podm.common.persistence.entity.EthernetSwitchPort;
import com.inspur.podm.common.intel.types.Id;
import com.inspur.podm.common.intel.types.net.MacAddress;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static com.inspur.podm.common.persistence.entity.EthernetSwitchPort.GET_ETHERNET_SWITCH_PORT_BY_NEIGHBOR_MAC;
import static com.inspur.podm.common.persistence.base.StatusControl.statusOf;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static javax.transaction.Transactional.TxType.MANDATORY;

@ApplicationScoped
public class EthernetSwitchPortDao extends Dao<EthernetSwitchPort> {
    @Transactional(MANDATORY)
    public EthernetSwitchPort getOrThrow(Id portId) {
        Optional<EthernetSwitchPort> expectedSwitchPort = tryFind(portId);
        if (!expectedSwitchPort.isPresent()) {
            throw new IllegalStateException("Target Ethernet Switch Port was not found!");
        }
        return expectedSwitchPort.get();
    }

    @Transactional(MANDATORY)
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