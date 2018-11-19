/*
 * Copyright (c) 2015-2018 inspur Corporation
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

package com.inspur.podm.service.rest.redfish.json.templates.assembly;

import static com.inspur.podm.api.business.services.context.ContextType.CHASSIS;
import static com.inspur.podm.api.business.services.context.ContextType.DRIVE;
import static com.inspur.podm.api.business.services.context.ContextType.SIMPLE_STORAGE;
import static com.inspur.podm.api.business.services.context.UriToContextConverter.getContextFromUri;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.odataid.ODataId;
import com.inspur.podm.api.business.services.redfish.requests.RequestedNode;
import com.inspur.podm.common.intel.types.MediaType;
import com.inspur.podm.common.intel.types.Protocol;

public final class RequestedLocalDriveImpl implements RequestedNode.LocalDrive {
    @JsonProperty("CapacityGiB")
    private BigDecimal capacityGib;

    @JsonProperty
    private MediaType type;

    @JsonProperty("MinRPM")
    private BigDecimal minRpm;

    @JsonProperty
    private String serialNumber;

    @JsonProperty("Interface")
    private Protocol protocol;

    private Context resourceContext;

    private Context chassisContext;

    @JsonProperty("FabricSwitch")
    private Boolean isFromFabricSwitch;

    @Override
    public BigDecimal getCapacityGib() {
        return capacityGib;
    }

    @Override
    public MediaType getType() {
        return type;
    }

    @Override
    public BigDecimal getMinRpm() {
        return minRpm;
    }

    @Override
    public String getSerialNumber() {
        return serialNumber;
    }

    @Override
    public Protocol getInterface() {
        return protocol;
    }

    @Override
    public Boolean isFromFabricSwitch() {
        return isFromFabricSwitch;
    }

    @JsonProperty("Resource")
    public void setResourceContext(ODataId resource) {
        if (resource == null) {
            return;
        }

        resourceContext = getContextFromUri(resource.toUri(), SIMPLE_STORAGE, DRIVE);
    }

    @Override
    public Context getResourceContext() {
        return resourceContext;
    }

    @JsonProperty("Chassis")
    public void setChassisContext(ODataId chassis) {
        if (chassis == null) {
            return;
        }

        chassisContext = getContextFromUri(chassis.toUri(), CHASSIS);
    }

    @Override
    public Context getChassisContext() {
        return chassisContext;
    }
}
