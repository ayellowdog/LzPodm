/*
 * Copyright (c) 2015-2018 Intel Corporation
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

package com.intel.podm.client.actions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.intel.podm.common.types.AdministrativeState;
import com.intel.podm.common.types.DcbxState;
import com.intel.podm.common.types.redfish.RedfishEthernetSwitchPort.PriorityFlowControl;

import java.net.URI;
import java.util.List;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Collections.unmodifiableList;

@JsonPropertyOrder({"AdministrativeState", "LinkSpeedMbps", "FrameSize", "Autosense", "DCBXState", "LLDPEnabled", "PriorityFlowControl", "Links"})
@JsonInclude(NON_DEFAULT)
public class EthernetSwitchPortResourceModificationRequest {
    @JsonProperty("AdministrativeState")
    private AdministrativeState administrativeState;

    @JsonProperty("LinkSpeedMbps")
    private Integer linkSpeed;

    @JsonProperty("FrameSize")
    private Integer frameSize;

    @JsonProperty("Autosense")
    private Boolean autosense;

    @JsonProperty("DCBXState")
    private DcbxState dcbxState;

    @JsonProperty("LLDPEnabled")
    private Boolean lldpEnabled;

    @JsonProperty("PriorityFlowControl")
    private PriorityFlowControl priorityFlowControl;

    @JsonProperty("Links")
    private EthernetSwitchPortLinks links;

    public void setAdministrativeState(AdministrativeState administrativeState) {
        this.administrativeState = administrativeState;
    }

    public void setLinkSpeed(Integer linkSpeed) {
        this.linkSpeed = linkSpeed;
    }

    public void setFrameSize(Integer frameSize) {
        this.frameSize = frameSize;
    }

    public void setAutosense(Boolean autosense) {
        this.autosense = autosense;
    }

    public void setDcbxState(DcbxState dcbxState) {
        this.dcbxState = dcbxState;
    }

    public void setLldpEnabled(Boolean lldpEnabled) {
        this.lldpEnabled = lldpEnabled;
    }

    public void setPriorityFlowControl(PriorityFlowControl priorityFlowControl) {
        if (priorityFlowControl != null) {
            this.priorityFlowControl = new PriorityFlowControlImpl(priorityFlowControl.getEnabled(), priorityFlowControl.getEnabledPriorities());
        }
    }

    public void setLinks(Set<URI> uris, URI primaryVlanUri) {
        EthernetSwitchPortLinks portLinks = new EthernetSwitchPortLinks();
        portLinks.setRequestedPortMembers(uris);
        portLinks.setPrimaryVlan(primaryVlanUri);

        if (portLinks.getPortMembers() != null || portLinks.getPrimaryVlan() != null) {
            links = portLinks;
        }
    }

    @JsonInclude(NON_NULL)
    @JsonPropertyOrder({"Enabled", "EnabledPriorities"})
    public static class PriorityFlowControlImpl implements PriorityFlowControl {
        @JsonProperty("Enabled")
        private Boolean enabled;

        @JsonProperty("EnabledPriorities")
        private List<Integer> enabledPriorities;

        public PriorityFlowControlImpl(Boolean enabled, List<Integer> enabledPriorities) {
            this.enabled = enabled;
            this.enabledPriorities = unmodifiableList(enabledPriorities);
        }

        @Override
        public Boolean getEnabled() {
            return enabled;
        }

        @Override
        public List<Integer> getEnabledPriorities() {
            return enabledPriorities;
        }
    }
}
