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

package com.intel.podm.business.redfish.services.assembly;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.EntityOperationException;
import com.intel.podm.business.entities.dao.DiscoverableEntityDao;
import com.intel.podm.business.entities.redfish.EthernetSwitchPortVlan;
import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.client.WebClient;
import com.intel.podm.client.WebClientBuilder;
import com.intel.podm.client.WebClientRequestException;

@Component
public class VlanTerminator {
    @Autowired
    private WebClientBuilder webClientBuilder;

    @Autowired
    private DiscoverableEntityDao discoverableEntityDao;

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteVlans(List<EthernetSwitchPortVlan> vlansToDelete) throws EntityOperationException {
        for (EthernetSwitchPortVlan vlan : vlansToDelete) {
            deleteVlan(vlan);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteVlan(EthernetSwitchPortVlan ethernetSwitchPortVlan) throws EntityOperationException {
        ExternalService service = ethernetSwitchPortVlan.getService();
        if (service == null) {
            throw new IllegalStateException("There is no Service associated with selected ethernet switch port vlan");
        }

        try (WebClient webClient = webClientBuilder.newInstance(service.getBaseUri()).retryable().build()) {
            webClient.delete(ethernetSwitchPortVlan.getSourceUri());
        } catch (WebClientRequestException e) {
            throw new EntityOperationException("Could not delete VLAN", e);
        }

        discoverableEntityDao.removeWithConnectedExternalLinks(ethernetSwitchPortVlan);
    }
}
