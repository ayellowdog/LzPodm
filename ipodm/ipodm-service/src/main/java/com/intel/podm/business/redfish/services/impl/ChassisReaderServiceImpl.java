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

package com.intel.podm.business.redfish.services.impl;

import static com.inspur.podm.api.business.dto.redfish.CollectionDto.Type.CHASSIS;
import static com.inspur.podm.api.business.services.context.Context.contextOf;
import static com.inspur.podm.api.business.services.context.SingletonContext.singletonContextOf;
import static com.intel.podm.common.types.redfish.ResourceNames.POWER_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.THERMAL_RESOURCE_NAME;
import static java.util.stream.Collectors.toList;
import static javax.transaction.Transactional.TxType.REQUIRED;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inspur.podm.api.business.ContextResolvingException;
import com.inspur.podm.api.business.dto.ChassisDto;
import com.inspur.podm.api.business.dto.redfish.CollectionDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.context.ContextType;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.inspur.podm.service.rest.redfish.json.templates.RedfishResourceAmazingWrapper;
import com.intel.podm.business.entities.dao.ChassisDao;
import com.intel.podm.business.entities.redfish.Chassis;
import com.intel.podm.business.redfish.services.EntityTreeTraverser;
import com.intel.podm.business.redfish.services.aggregation.ChassisMerger;
import com.intel.podm.common.types.Id;

@Service("chassisReaderService")
class ChassisReaderServiceImpl implements ReaderService<ChassisDto> {
    @Autowired
    private ChassisDao chassisDao;

    @Autowired
    private EntityTreeTraverser traverser;

    @Autowired
    private ChassisMerger chassisMerger;

    @Transactional(REQUIRED)
    @Override
    public CollectionDto getCollection(Context serviceRootContext) throws ContextResolvingException {
    	List<Id> findAllChassisFromPrimaryDataSource = chassisDao.findAllChassisFromPrimaryDataSource();
		List<Context> contexts = findAllChassisFromPrimaryDataSource.stream()
				.map(id -> contextOf(id, ContextType.CHASSIS)).sorted().collect(toList());
		return new CollectionDto(CHASSIS, contexts);
    }

    @Transactional(REQUIRED)
    @Override
    public ChassisDto getResource(Context context) throws ContextResolvingException {
        Chassis chassis = (Chassis) traverser.traverse(context);

        // Multi-source resources sanity check
        if (chassis.isComplementary()) {
            throw new ContextResolvingException("Specified resource is not a primary resource representation!", context, null);
        }

        ChassisDto dto = chassisMerger.toDto(chassis);
        if (chassis.getThermal() != null) {
            dto.setThermal(singletonContextOf(context, THERMAL_RESOURCE_NAME));
        }
        if (chassis.getPower() != null) {
            dto.setPower(singletonContextOf(context, POWER_RESOURCE_NAME));
        }
        return dto;
    }
}
