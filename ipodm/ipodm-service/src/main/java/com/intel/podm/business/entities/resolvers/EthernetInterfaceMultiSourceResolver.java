/*
 * Copyright (c) 2017-2018 Intel Corporation
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

package com.intel.podm.business.entities.resolvers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.intel.podm.business.entities.dao.EthernetInterfaceDao;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.EthernetInterface;

//@Dependent
@Component
public class EthernetInterfaceMultiSourceResolver extends MultiSourceEntityResolver<EthernetInterface> {
    private final EthernetInterfaceDao ethernetInterfaceDao;

    @Autowired
    EthernetInterfaceMultiSourceResolver(EthernetInterfaceDao ethernetInterfaceDao) {
        super(EthernetInterface.class);
        this.ethernetInterfaceDao = ethernetInterfaceDao;
    }

    @Override
    protected Optional<EthernetInterface> findPrimaryEntity(EthernetInterface complementaryEthernetInterface) {
        return ethernetInterfaceDao.findPrimaryEthernetInterface(complementaryEthernetInterface);
    }

    @Override
    public String createMultiSourceDiscriminator(EthernetInterface ethernetInterface) {
        ComputerSystem computerSystem = ethernetInterface.getComputerSystem();
        if (computerSystem == null) {
            return null;
        }

        return computerSystem.getMultiSourceDiscriminator() + ethernetInterface.getMacAddress().toString();
    }
}
