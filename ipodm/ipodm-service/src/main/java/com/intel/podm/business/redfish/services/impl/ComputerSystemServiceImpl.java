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

import static com.inspur.podm.api.business.dto.redfish.CollectionDto.Type.SYSTEM;
import static com.inspur.podm.api.business.services.context.Context.contextOf;
import static com.inspur.podm.api.business.services.context.ContextType.COMPUTER_SYSTEM;
import static com.inspur.podm.api.business.services.context.SingletonContext.singletonContextOf;
import static com.intel.podm.common.types.redfish.ResourceNames.COMPUTER_SYSTEM_METRICS_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.ETHERNET_INTERFACES_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.MEMORY_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.NETWORK_INTERFACES_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.PROCESSORS_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.SIMPLE_STORAGE_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.STORAGE_RESOURCE_NAME;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.ContextResolvingException;
import com.inspur.podm.api.business.dto.ComputerSystemDto;
import com.inspur.podm.api.business.dto.redfish.CollectionDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.intel.podm.business.entities.dao.ComputerSystemDao;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.redfish.services.EntityTreeTraverser;
import com.intel.podm.business.redfish.services.aggregation.ComputerSystemMerger;

@Service("ComputerSystemService")
class ComputerSystemServiceImpl implements ReaderService<ComputerSystemDto> {
    @Autowired
    private ComputerSystemDao computerSystemDao;

    @Autowired
    private EntityTreeTraverser traverser;

    @Autowired
    private ComputerSystemMerger computerSystemMerger;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CollectionDto getCollection(Context serviceRootContext) throws ContextResolvingException {
        List<Context> contexts = computerSystemDao.findAllComputerSystemsFromPrimaryDataSource().stream()
            .map(id -> contextOf(id, COMPUTER_SYSTEM)).sorted().collect(toList());
        return new CollectionDto(SYSTEM, contexts);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ComputerSystemDto getResource(Context context) throws ContextResolvingException {
        ComputerSystem system = (ComputerSystem) traverser.traverse(context);

        // Multi-source resources sanity check
        if (system.isComplementary()) {
            throw new ContextResolvingException("Specified resource is not a primary resource representation!", context, null);
        }

        ComputerSystemDto dto = computerSystemMerger.toDto(system);
        setSingletonContexts(context, dto);
        setComputerSystemMetrics(context, system, dto);

        ComputerSystemDto.Actions actions = dto.getActions();
        actions.getReset().setTarget(singletonContextOf(context, "Actions/ComputerSystem.Reset"));
        actions.getOem().getStartDeepDiscovery().setTarget(singletonContextOf(context, "Actions/Oem/Intel.Oem.StartDeepDiscovery"));
        actions.getOem().getChangeTpmState().setTarget(singletonContextOf(context, "Actions/Oem/Intel.Oem.ChangeTPMState"));

        return dto;
    }

    private void setSingletonContexts(Context context, ComputerSystemDto dto) {
        dto.setProcessors(singletonContextOf(context, PROCESSORS_RESOURCE_NAME));
        dto.setMemory(singletonContextOf(context, MEMORY_RESOURCE_NAME));
        dto.setSimpleStorage(singletonContextOf(context, SIMPLE_STORAGE_RESOURCE_NAME));
        dto.setStorage(singletonContextOf(context, STORAGE_RESOURCE_NAME));
        dto.setEthernetInterfaces(singletonContextOf(context, ETHERNET_INTERFACES_RESOURCE_NAME));
        dto.setNetworkInterfaces(singletonContextOf(context, NETWORK_INTERFACES_RESOURCE_NAME));
    }

    private void setComputerSystemMetrics(Context context, ComputerSystem system, ComputerSystemDto dto) {
        if (system.getComputerSystemMetrics() != null) {
            dto.getOem().getRackScaleOem().setComputerSystemMetrics(singletonContextOf(context, COMPUTER_SYSTEM_METRICS_RESOURCE_NAME));
        }
    }
}
