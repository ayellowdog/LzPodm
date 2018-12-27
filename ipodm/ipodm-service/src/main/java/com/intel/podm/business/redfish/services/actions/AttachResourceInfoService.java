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

import static com.inspur.podm.api.business.dto.actions.actionInfo.ParametersDtoBuilder.asProtocolParameterDto;
import static com.inspur.podm.api.business.dto.actions.actionInfo.ParametersDtoBuilder.asResourceParameterDto;
import static com.intel.podm.business.entities.redfish.base.StatusControl.statusOf;
import static com.intel.podm.business.redfish.services.ContextCollections.asDriveContexts;
import static com.intel.podm.business.redfish.services.ContextCollections.asEndpointContexts;
import static com.intel.podm.business.redfish.services.ContextCollections.asVolumeContexts;
import static com.intel.podm.common.types.Protocol.ISCSI;
import static com.intel.podm.common.types.Protocol.NVME;
import static com.intel.podm.common.types.Protocol.PCIE;
import static com.intel.podm.common.types.actions.ActionInfoNames.ATTACH_RESOURCE_ACTION_INFO;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.ContextResolvingException;
import com.inspur.podm.api.business.dto.actions.actionInfo.ActionInfoDto;
import com.inspur.podm.api.business.dto.actions.actionInfo.ParameterDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.odataid.ODataId;
import com.inspur.podm.api.business.services.redfish.odataid.ODataIdFromContextHelper;
import com.intel.podm.business.entities.dao.DriveDao;
import com.intel.podm.business.entities.dao.GenericDao;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.Drive;
import com.intel.podm.business.entities.redfish.Endpoint;
import com.intel.podm.business.entities.redfish.Volume;
import com.intel.podm.business.redfish.services.helpers.VolumeHelper;
import com.intel.podm.common.types.Protocol;

@Component
public class AttachResourceInfoService {

    @Autowired
    private DriveDao driveDao;

    @Autowired
    private GenericDao genericDao;

    @Autowired
    private VolumeHelper volumeHelper;

    @Transactional(propagation = Propagation.MANDATORY)
    public ActionInfoDto getActionInfo(ComposedNode composedNode) throws ContextResolvingException {
        ActionInfoDto actionInfoDto = new ActionInfoDto();
        actionInfoDto.setId(ATTACH_RESOURCE_ACTION_INFO);
        actionInfoDto.setName("Attach Resource ActionInfo");
        List<ParameterDto> parameterList = new ArrayList<>();
        parameterList.addAll(getParameters(composedNode));
        actionInfoDto.addParameters(parameterList);

        return actionInfoDto;
    }

    private Collection<? extends ParameterDto> getParameters(ComposedNode composedNode) {
        Set<Drive> drives = getDrivesAllowableToAttach(composedNode);
        Set<Volume> volumes = getVolumesAllowableToAttach();
        Set<Endpoint> endpoints = getEndpointsAllowableToAttach();

        Set<ODataId> oDataIdValues = new HashSet<>(getODataId(asDriveContexts(drives)));
        oDataIdValues.addAll(getODataId(asVolumeContexts(volumes)));
        oDataIdValues.addAll(getODataId(asEndpointContexts(endpoints)));

        Set<Protocol> protocolValues = new HashSet<>(getProtocolsForDrives(drives));
        protocolValues.addAll(getProtocolsForVolumes(volumes));
        protocolValues.addAll(getProtocolsForEndpoints(endpoints));

        return asList(asResourceParameterDto(oDataIdValues), asProtocolParameterDto(protocolValues));
    }

    private Set<ODataId> getODataId(Set<Context> contexts) {
        return contexts.stream().map(ODataIdFromContextHelper::asOdataId)
            .collect(toSet());
    }

    private Set<Protocol> getProtocolsForVolumes(Set<Volume> volumes) {
        return volumes.stream().map(volumeHelper::retrieveProtocolFromVolume).collect(toSet());
    }

    private Set<Protocol> getProtocolsForDrives(Set<Drive> drives) {
        if (!drives.isEmpty()) {
            return singleton(PCIE);
        }
        return emptySet();
    }

    private Set<Protocol> getProtocolsForEndpoints(Set<Endpoint> endpoints) {
        return endpoints.stream().map(Endpoint::getProtocol).collect(toSet());
    }

    private Set<Endpoint> getEndpointsAllowableToAttach() {
        return genericDao.findAll(Endpoint.class).stream()
            .filter(excludeIscsiPortEndpoint())
            .filter(excludeNvmePcieEndpoint())
            .filter(endpoint -> endpoint.getZone() == null)
            .filter(endpoint -> !endpoint.getMetadata().isAllocated())
            .filter(endpoint -> statusOf(endpoint).isEnabled().isHealthy().verify())
            .collect(toSet());
    }

    private Set<Volume> getVolumesAllowableToAttach() {
        return genericDao.findAll(Volume.class).stream()
            .filter(volume -> volume.getEndpoints().isEmpty())
            .filter(volume -> volume.getStorageService() != null)
            .filter(excludeIscsiPortVolume())
            .filter(volume -> !volume.getMetadata().isAllocated())
            .filter(volume -> statusOf(volume).isEnabled().isHealthy().verify())
            .collect(toSet());
    }

    private Predicate<Endpoint> excludeIscsiPortEndpoint() {
        return endpoint -> !ISCSI.equals(endpoint.getProtocol());
    }

    private Predicate<Endpoint> excludeNvmePcieEndpoint() {
        return endpoint -> !NVME.equals(endpoint.getProtocol());
    }

    private Predicate<Volume> excludeIscsiPortVolume() {
        return volume -> !ISCSI.equals(volumeHelper.retrieveProtocolFromVolume(volume));
    }

    private Set<Drive> getDrivesAllowableToAttach(ComposedNode composedNode) {
        return driveDao.getAchievablePcieDrives(composedNode.getComputerSystem());
    }
}
