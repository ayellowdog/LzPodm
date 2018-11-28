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

package com.inspur.podm.service.service.redfish.aggregation;

import static java.util.Collections.emptyList;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.inspur.podm.api.business.dto.EthernetInterfaceDto;
import com.inspur.podm.common.persistence.entity.ComputerSystem;
import com.inspur.podm.common.persistence.entity.EthernetInterface;
import com.inspur.podm.service.dao.EthernetInterfaceDao;

@ApplicationScoped
public class EthernetInterfaceMerger extends DiscoverableEntityDataMerger<EthernetInterface, EthernetInterfaceDto> {
    @Inject
    private EthernetInterfaceDao ethernetInterfaceDao;

    @Override
    protected List<EthernetInterface> getMultiSourceRepresentations(EthernetInterface entity) {
        ComputerSystem computerSystem = entity.getComputerSystem();
        // merge only ethernet interfaces under Computer System
        if (computerSystem != null) {
            return ethernetInterfaceDao.findComplementaryEthernetInterfaces(entity);
        }

        return emptyList();
    }
}