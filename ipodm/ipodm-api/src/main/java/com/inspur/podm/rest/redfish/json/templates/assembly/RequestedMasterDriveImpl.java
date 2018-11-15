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

package com.inspur.podm.rest.redfish.json.templates.assembly;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspur.podm.business.services.context.Context;
import com.inspur.podm.business.services.redfish.odataid.ODataId;
import com.inspur.podm.business.services.redfish.requests.RequestedNode;
import com.inspur.podm.common.types.ReplicaType;

import static com.inspur.podm.business.services.context.ContextType.VOLUME;
import static com.inspur.podm.business.services.context.UriToContextConverter.getContextFromUri;

public final class RequestedMasterDriveImpl implements RequestedNode.RemoteDrive.MasterDrive {
    @JsonProperty
    private ReplicaType type;

    private Context volumeContext;

    @Override
    public ReplicaType getType() {
        return type;
    }

    @JsonProperty("Resource")
    public void setResourceContext(ODataId resource) {
        if (resource == null) {
            return;
        }

        volumeContext = getContextFromUri(resource.toUri(), VOLUME);
    }

    @Override
    public Context getResourceContext() {
        return volumeContext;
    }
}
