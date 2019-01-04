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

package com.intel.podm.business.redfish.services.impl;

import static com.inspur.podm.api.business.services.context.Context.contextOf;
import static com.inspur.podm.api.business.services.context.SingletonContext.singletonContextOf;
import static com.intel.podm.common.types.redfish.ResourceNames.ETHERNET_INTERFACES_RESOURCE_NAME;
import static com.intel.podm.common.types.redfish.ResourceNames.NETWORK_PROTOCOL_RESOURCE_NAME;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.ContextResolvingException;
import com.inspur.podm.api.business.dto.ManagerDto;
import com.inspur.podm.api.business.dto.redfish.CollectionDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.context.ContextType;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.intel.podm.business.entities.dao.ManagerDao;
import com.intel.podm.business.entities.redfish.Manager;
import com.intel.podm.business.redfish.services.EntityTreeTraverser;
import com.intel.podm.business.redfish.services.mappers.EntityToDtoMapper;

@Service("ManagerReaderService")
class ManagerReaderServiceImpl implements ReaderService<ManagerDto> {
    @Autowired
    EntityTreeTraverser traverser;

    @Autowired
    EntityToDtoMapper entityToDtoMapper;

    @Autowired
    private ManagerDao managerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public CollectionDto getCollection(Context serviceRootContext) throws ContextResolvingException {
        List<Context> contexts = managerDao.getAllManagerIds().stream().map(id -> contextOf(id, ContextType.MANAGER)).sorted().collect(toList());
        return new CollectionDto(CollectionDto.Type.MANAGER, contexts);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ManagerDto getResource(Context context) throws ContextResolvingException {
        Manager manager = (Manager) traverser.traverse(context);

        ManagerDto dto = (ManagerDto) entityToDtoMapper.map(manager);
        dto.setEthernetInterfaces(singletonContextOf(context, ETHERNET_INTERFACES_RESOURCE_NAME));
        if (manager.getNetworkProtocol() != null) {
            dto.setNetworkProtocol(singletonContextOf(context, NETWORK_PROTOCOL_RESOURCE_NAME));
        }
        return dto;
    }
}
