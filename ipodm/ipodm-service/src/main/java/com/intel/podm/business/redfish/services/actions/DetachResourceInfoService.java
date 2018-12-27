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

package com.intel.podm.business.redfish.services.actions;

import static com.inspur.podm.api.business.dto.actions.actionInfo.ParametersDtoBuilder.asResourceParameterDto;
import static com.intel.podm.business.redfish.services.ContextCollections.asDriveContexts;
import static com.intel.podm.business.redfish.services.ContextCollections.asEndpointContexts;
import static com.intel.podm.business.redfish.services.ContextCollections.asVolumeContexts;
import static com.intel.podm.common.types.Protocol.ISCSI;
import static com.intel.podm.common.types.actions.ActionInfoNames.DETACH_RESOURCE_ACTION_INFO;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.dto.actions.actionInfo.ActionInfoDto;
import com.inspur.podm.api.business.dto.actions.actionInfo.ParameterDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.odataid.ODataId;
import com.inspur.podm.api.business.services.redfish.odataid.ODataIdFromContextHelper;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.Endpoint;
import com.intel.podm.business.entities.redfish.Volume;
import com.intel.podm.business.redfish.services.helpers.VolumeHelper;

@Component
public class DetachResourceInfoService {

    @Autowired
    private VolumeHelper volumeHelper;

    @Transactional(propagation = Propagation.MANDATORY)
    public ActionInfoDto getActionInfo(ComposedNode composedNode) {
        ActionInfoDto actionInfoDto = new ActionInfoDto();
        actionInfoDto.setId(DETACH_RESOURCE_ACTION_INFO);
        actionInfoDto.setName("Detach Resource ActionInfo");
        List<ParameterDto> parameterList = new ArrayList<>();
        parameterList.add(asResourceParameterDto(getAllowableValuesForResource(composedNode)));
        actionInfoDto.addParameters(parameterList);

        return actionInfoDto;
    }

    private Set<ODataId> getAllowableValuesForResource(ComposedNode composedNode) {
        Set<Context> resources = new HashSet<>();
        Set<Volume> volumes = composedNode.getVolumes().
            stream().filter(excludeIscsiPortVolume()).collect(toSet());
        resources.addAll(asVolumeContexts(volumes));
        Set<Endpoint> endpoints = composedNode.getEndpoints().
            stream().filter(excludeIscsiPortEndpoint()).collect(toSet());
        resources.addAll(asEndpointContexts(endpoints));
        resources.addAll(asDriveContexts(composedNode.getDrives()));

        return resources.stream().map(ODataIdFromContextHelper::asOdataId).collect(toSet());
    }

    private Predicate<Endpoint> excludeIscsiPortEndpoint() {
        return endpoint -> !ISCSI.equals(endpoint.getProtocol());
    }

    private Predicate<Volume> excludeIscsiPortVolume() {
        return volume -> !ISCSI.equals(volumeHelper.retrieveProtocolFromVolume(volume));
    }
}
