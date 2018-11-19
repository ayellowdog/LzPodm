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

package com.inspur.podm.service.rest.redfish.json.templates.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspur.podm.common.intel.types.PortMode;
import com.inspur.podm.common.intel.types.redfish.RedfishEthernetSwitchPort;

@SuppressWarnings({"checkstyle:VisibilityModifier"})
public class CreateEthernetSwitchPortActionJson implements RedfishEthernetSwitchPort {
    @JsonProperty("Name")
    public String name;

    @JsonProperty("PortId")
    public String portId;

    @JsonProperty("PortMode")
    public PortMode portMode;

    @JsonProperty("Links")
    private EthernetSwitchPortLinksJson links = new EthernetSwitchPortLinksJson();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPortId() {
        return portId;
    }

    @Override
    public PortMode getPortMode() {
        return portMode;
    }

    @Override
    public RedfishEthernetSwitchPort.Links getLinks() {
        return links;
    }
}
