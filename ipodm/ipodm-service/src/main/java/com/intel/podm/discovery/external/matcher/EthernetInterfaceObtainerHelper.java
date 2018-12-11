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

package com.intel.podm.discovery.external.matcher;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.EthernetInterface;
import com.intel.podm.business.entities.redfish.base.Entity;
import com.intel.podm.client.WebClientRequestException;
import com.intel.podm.client.resources.redfish.ComputerSystemResource;
import com.intel.podm.client.resources.redfish.EthernetInterfaceResource;

//@Dependent
@Component
public class EthernetInterfaceObtainerHelper implements EntityObtainerHelper<EthernetInterfaceResource> {
    @Override
    public ComputerSystemResource findComputerSystemResourceFor(EthernetInterfaceResource resource) throws WebClientRequestException {
        return (ComputerSystemResource) resource.getComputerSystem().get();
    }

    @Override
    public Optional<EthernetInterface> findEntityFor(ComputerSystem computerSystem, EthernetInterfaceResource resource) {
        return computerSystem.getEthernetInterfaces().stream()
                .filter(eth -> Objects.equals(eth.getMacAddress(), resource.getMacAddress().orElse(null)))
                .findFirst();
    }

    @Override
    public Class<? extends Entity> getEntityClass() {
        return EthernetInterface.class;
    }

    @Override
    public Class<EthernetInterfaceResource> getResourceClass() {
        return EthernetInterfaceResource.class;
    }
}
