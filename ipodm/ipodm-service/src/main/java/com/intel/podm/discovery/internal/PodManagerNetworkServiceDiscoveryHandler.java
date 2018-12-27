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

package com.intel.podm.discovery.internal;

import static com.intel.podm.common.types.Id.id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.intel.podm.business.entities.dao.GenericDao;
import com.intel.podm.business.entities.redfish.Manager;
import com.intel.podm.business.entities.redfish.NetworkProtocol;
import com.intel.podm.config.network.NetworkConfigurationReader;
import com.intel.podm.config.network.dto.NetworkServiceDto;

//@Dependent
@Component
class PodManagerNetworkServiceDiscoveryHandler {

    @Autowired
    private NetworkConfigurationReader networkConfigurationReader;

    @Autowired
    private GenericDao genericDao;

    public void addNetworkService(Manager manager) {
        NetworkServiceDto networkServiceDto = networkConfigurationReader.readConfigurationOrDefault(
            "network-service", NetworkServiceDto.class, NetworkServiceDto::new
        );

        NetworkProtocol networkProtocol = genericDao.create(NetworkProtocol.class);
        networkProtocol.setId(id("network-protocol"));
        networkProtocol.setName(networkServiceDto.getName());
        networkProtocol.setDescription(networkServiceDto.getDescription());
        networkProtocol.setStatus(networkServiceDto.getStatus());
        networkProtocol.setHostName(networkServiceDto.getHostName());
        networkProtocol.setFqdn(networkServiceDto.getFqdn());
        manager.setNetworkProtocol(networkProtocol);
    }
}
