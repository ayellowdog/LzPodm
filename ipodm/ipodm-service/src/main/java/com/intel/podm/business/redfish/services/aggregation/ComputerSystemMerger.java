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

package com.intel.podm.business.redfish.services.aggregation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inspur.podm.api.business.dto.ComputerSystemDto;
import com.intel.podm.business.entities.dao.ComputerSystemDao;
import com.intel.podm.business.entities.redfish.ComputerSystem;

@Component
public class ComputerSystemMerger extends DiscoverableEntityDataMerger<ComputerSystem, ComputerSystemDto> {
    @Autowired
    private ComputerSystemDao computerSystemDao;

    @Override
    protected List<ComputerSystem> getMultiSourceRepresentations(ComputerSystem entity) {
        return computerSystemDao.findComplementarySystems(entity);
    }

}
