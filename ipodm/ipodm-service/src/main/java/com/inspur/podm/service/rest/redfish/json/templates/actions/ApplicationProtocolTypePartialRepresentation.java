/*
 * Copyright (c) 2018 inspur Corporation
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspur.podm.common.intel.types.ProtocolType;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.inspur.podm.common.intel.types.redfish.RedfishEthernetSwitch.DcbxConfiguration.ApplicationProtocolType;

@JsonInclude(NON_NULL)
public class ApplicationProtocolTypePartialRepresentation implements ApplicationProtocolType {

    @JsonProperty("Priority")
    private Long priority;

    @JsonProperty("Protocol")
    private ProtocolType protocol;

    @JsonProperty("Port")
    private Long port;

    @Override
    public Long getPriority() {
        return priority;
    }

    @Override
    public ProtocolType getProtocol() {
        return protocol;
    }

    @Override
    public Long getPort() {
        return port;
    }
}
