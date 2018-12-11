/*
 * Copyright (c) 2016-2018 inspur Corporation
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

package com.inspur.podm.service.rest.redfish.json.templates.actions;

import static com.inspur.podm.api.business.services.context.UriToContextConverter.getContextFromUri;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.odataid.ODataId;
import com.inspur.podm.api.business.services.redfish.requests.AttachResourceRequest;
import com.intel.podm.common.types.Protocol;

public class AttachResourceJson implements AttachResourceRequest {
    @JsonProperty("Resource")
    private Context resourceContext;
    @JsonProperty("Protocol")
    private Protocol protocol;

    public void setResourceContext(ODataId oDataId) {
        if (oDataId == null) {
            return;
        }

        resourceContext = getContextFromUri(oDataId.toUri());
    }

    @Override
    public Context getResourceContext() {
        return resourceContext;
    }

    @Override
    public Protocol getProtocol() {
        return protocol;
    }
}
