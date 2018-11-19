/*
 * Copyright (c) 2017-2018 inspur Corporation
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.odataid.ODataId;
import com.inspur.podm.common.intel.types.redfish.ZoneCreationInterface;

import static com.inspur.podm.api.business.services.context.ContextType.ENDPOINT;
import static com.inspur.podm.api.business.services.context.UriToContextConverter.getContextFromUri;

import java.util.HashSet;
import java.util.Set;

public class CreateZoneJson implements ZoneCreationInterface {
    @JsonProperty("Name")
    private String name;

    @JsonProperty("Links")
    private Links links = new Links();

    public CreateZoneJson() {
    }

    public CreateZoneJson(String name, Links links) {
        this.name = name;
        this.links = links;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<Context> getEndpoints() {
        return links.endpoints;
    }

    public static class Links {
        private Set<Context> endpoints = new HashSet<>();

        public Links() {
        }

        @JsonProperty("Endpoints")
        public void setEndpoints(Set<ODataId> oDataIds) {
            if (oDataIds == null) {
                return;
            }
            oDataIds.stream()
                .map(oDataId -> getContextFromUri(oDataId.toUri(), ENDPOINT))
                .forEach(endpoints::add);
        }

        public Set<Context> getEndpoints() {
            return endpoints;
        }
    }
}
