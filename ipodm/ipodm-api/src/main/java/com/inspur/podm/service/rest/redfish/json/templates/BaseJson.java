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

package com.inspur.podm.service.rest.redfish.json.templates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.JsonNode;
import com.inspur.podm.api.business.services.redfish.odataid.ODataId;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"checkstyle:VisibilityModifier"})
public abstract class BaseJson {
    @JsonProperty("@odata.type")
    public final String oDataType;

    @JsonProperty("@odata.context")
    public URI oDataContext;

    @JsonUnwrapped
    @JsonProperty("@odata.id")
    public ODataId oDataId;

    @JsonIgnore
    public Map<String, JsonNode> unknownOems = new HashMap<>();

    protected BaseJson(String oDataType) {
        this.oDataType = oDataType;
    }
}
