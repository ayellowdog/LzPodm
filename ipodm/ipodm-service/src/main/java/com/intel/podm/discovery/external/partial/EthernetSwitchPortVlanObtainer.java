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

package com.intel.podm.discovery.external.partial;

import com.intel.podm.business.entities.dao.DiscoverableEntityDao;
import com.intel.podm.business.entities.redfish.EthernetSwitchPortVlan;
import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.client.WebClient;
import com.intel.podm.client.WebClientBuilder;
import com.intel.podm.client.WebClientRequestException;
import com.intel.podm.client.resources.redfish.EthernetSwitchPortVlanResource;
import com.intel.podm.common.types.Id;
import com.intel.podm.mappers.redfish.EthernetSwitchPortVlanMapper;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static com.intel.podm.common.utils.Contracts.requiresNonNull;
import static javax.transaction.Transactional.TxType.MANDATORY;

//@Dependent
@Component
public class EthernetSwitchPortVlanObtainer {
//    @Inject
	@Autowired
    private EthernetSwitchPortVlanMapper mapper;

//    @Inject
	@Autowired
    private WebClientBuilder webClientBuilder;

	@Autowired
    private DiscoverableEntityDao discoverableEntityDao;

    @Transactional(MANDATORY)
    public Set<EthernetSwitchPortVlan> discoverEthernetSwitchPortVlans(ExternalService service, Set<URI> vlanUris) throws WebClientRequestException {
        Set<EthernetSwitchPortVlan> vlans = new HashSet<>();
        for (URI vlanUri : vlanUris) {
            // TODO: RSASW-8088
            vlans.add(discoverEthernetSwitchPortVlan(service, vlanUri));
        }
        return vlans;
    }

    @Transactional(MANDATORY)
    public EthernetSwitchPortVlan discoverEthernetSwitchPortVlan(ExternalService service, URI vlanUri) throws WebClientRequestException {
        requiresNonNull(service, "service", "There is no Service associated with selected switch port");

        try (WebClient webClient = webClientBuilder.newInstance(service.getBaseUri()).retryable().build()) {
            EthernetSwitchPortVlanResource psmeVlan = (EthernetSwitchPortVlanResource) webClient.get(vlanUri);
            Id entityId = psmeVlan.getGlobalId(service.getTheId());
            EthernetSwitchPortVlan target = discoverableEntityDao.findOrCreateEntity(service, entityId, psmeVlan.getUri(), EthernetSwitchPortVlan.class);
            mapper.map(psmeVlan, target);

            return target;
        }
    }
}
