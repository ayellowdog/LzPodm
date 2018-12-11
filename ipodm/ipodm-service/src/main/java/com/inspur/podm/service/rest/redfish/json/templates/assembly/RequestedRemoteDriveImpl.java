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

import static com.inspur.podm.api.business.services.context.ContextType.ENDPOINT;
import static com.inspur.podm.api.business.services.context.ContextType.VOLUME;
import static com.inspur.podm.api.business.services.context.UriToContextConverter.getContextFromUri;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.odataid.ODataId;
import com.inspur.podm.api.business.services.redfish.requests.RequestedNode;
import com.intel.podm.common.types.Protocol;

public final class RequestedRemoteDriveImpl implements RequestedNode.RemoteDrive {
    @JsonProperty("CapacityGiB")
    private BigDecimal capacityGib;

    @JsonProperty("Resource")
    private Context resourceContext;

    @JsonProperty("Protocol")
    private Protocol protocol;

    @JsonProperty
    private RequestedMasterDriveImpl master;

    @Override
    public BigDecimal getCapacityGib() {
        return capacityGib;
    }

    @Override
    public RequestedMasterDriveImpl getMaster() {
        return master;
    }

    @Override
    public Context getResourceContext() {
        return resourceContext;
    }

    public void setResourceContext(ODataId resource) {
        if (resource == null) {
            return;
        }

        resourceContext = getContextFromUri(resource.toUri(), VOLUME, ENDPOINT);
    }

    @Override
    public Protocol getProtocol() {
        return protocol;
    }
}
