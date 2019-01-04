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

import static com.inspur.podm.api.business.dto.redfish.CollectionDto.Type.ETHERNET_SWITCH_PORT_VLAN;
import static com.inspur.podm.api.business.services.context.ContextType.ETHERNET_INTERFACE;
import static com.inspur.podm.api.business.services.context.ContextType.ETHERNET_SWITCH_PORT;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.ContextResolvingException;
import com.inspur.podm.api.business.dto.VlanNetworkInterfaceDto;
import com.inspur.podm.api.business.dto.redfish.CollectionDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.context.ContextType;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.intel.podm.business.entities.redfish.EthernetInterface;
import com.intel.podm.business.entities.redfish.EthernetSwitchPort;
import com.intel.podm.business.entities.redfish.EthernetSwitchPortVlan;
import com.intel.podm.business.redfish.services.Contexts;
import com.intel.podm.business.redfish.services.EntityTreeTraverser;
import com.intel.podm.business.redfish.services.aggregation.MultiSourceEntityTreeTraverser;
import com.intel.podm.business.redfish.services.mappers.EntityToDtoMapper;

@Service("VlanNetworkInterfaceReaderService")
@SuppressWarnings({"checkstyle:ClassFanOutComplexity"})
class VlanNetworkInterfaceReaderServiceImpl implements ReaderService<VlanNetworkInterfaceDto> {
    @Autowired
    private EntityTreeTraverser traverser;

    @Autowired
    private MultiSourceEntityTreeTraverser multiTraverser;

    @Autowired
    private EntityToDtoMapper entityToDtoMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public CollectionDto getCollection(Context context) throws ContextResolvingException {
        ContextType type = context.getType();
        if (Objects.equals(ETHERNET_SWITCH_PORT, type)) {
            EthernetSwitchPort port = (EthernetSwitchPort) traverser.traverse(context);
            List<Context> contexts = port.getEthernetSwitchPortVlans().stream().map(Contexts::toContext).sorted().collect(toList());
            return new CollectionDto(ETHERNET_SWITCH_PORT_VLAN, contexts);
        } else if (Objects.equals(ETHERNET_INTERFACE, type)) {
            EthernetInterface iface = (EthernetInterface) multiTraverser.traverse(context);
            List<Context> contexts = iface.getEthernetSwitchPortVlans().stream().map(Contexts::toContext).sorted().collect(toList());
            return new CollectionDto(ETHERNET_SWITCH_PORT_VLAN, contexts);
        }
        throw new ContextResolvingException(context);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public VlanNetworkInterfaceDto getResource(Context context) throws ContextResolvingException {
        EthernetSwitchPortVlan vlan = (EthernetSwitchPortVlan) traverser.traverse(context);
        return (VlanNetworkInterfaceDto) entityToDtoMapper.map(vlan);
    }
}
