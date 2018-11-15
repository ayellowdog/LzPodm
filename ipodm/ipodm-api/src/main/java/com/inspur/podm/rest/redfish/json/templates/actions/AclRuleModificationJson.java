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

package com.inspur.podm.rest.redfish.json.templates.actions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.inspur.podm.business.services.context.Context;
import com.inspur.podm.business.services.redfish.odataid.ODataId;
import com.inspur.podm.common.types.ActionType;
import com.inspur.podm.common.types.MirrorType;
import com.inspur.podm.common.types.Ref;
import com.inspur.podm.common.types.redfish.RedfishEthernetSwitchAclRule;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.fasterxml.jackson.annotation.Nulls.AS_EMPTY;
import static com.inspur.podm.business.services.context.ContextType.ETHERNET_SWITCH_PORT;
import static com.inspur.podm.business.services.context.UriToContextConverter.getContextFromUri;
import static com.inspur.podm.common.types.Ref.unassigned;

public final class AclRuleModificationJson implements RedfishEthernetSwitchAclRule {
    @JsonSetter(value = "RuleId", nulls = AS_EMPTY)
    private Ref<Integer> ruleId = unassigned();

    @JsonSetter(value = "Action", nulls = AS_EMPTY)
    private Ref<ActionType> actionType = unassigned();

    @JsonIgnore
    private Ref<Context> forwardMirrorInterface = unassigned();

    @JsonIgnore
    private Ref<Set<Context>> mirrorPortRegions = unassigned();

    @JsonSetter(value = "MirrorType", nulls = AS_EMPTY)
    private Ref<MirrorType> mirrorType = unassigned();

    @JsonSetter(value = "Condition", nulls = AS_EMPTY)
    private Ref<AclRuleConditionImpl> condition = unassigned();

    @Override
    public Ref<Integer> getRuleId() {
        return ruleId;
    }

    @Override
    public Ref<ActionType> getActionType() {
        return actionType;
    }

    @Override
    public Ref<Context> getForwardMirrorInterface() {
        return forwardMirrorInterface;
    }

    @JsonSetter(value = "ForwardMirrorInterface")
    public void setForwardMirrorInterface(ODataId mirrorInterface) {
        if (mirrorInterface == null) {
            return;
        }
        forwardMirrorInterface = Ref.of(getContextFromUri(mirrorInterface.toUri()));
    }

    @Override
    public Ref<Set<Context>> getMirrorPortRegions() {
        return mirrorPortRegions;
    }

    @JsonSetter(value = "MirrorPortRegion", nulls = AS_EMPTY)
    public void setMirrorPortRegions(Set<ODataId> portRegions) {
        if (portRegions == null) {
            return;
        }
        Set<Context> contextList = new HashSet<>();
        for (ODataId port : portRegions) {
            if (port == null) {
                throw new RuntimeException("Port is not set");
            }
            Context switchContext = getContextFromUri(port.toUri(), ETHERNET_SWITCH_PORT);
            contextList.add(switchContext);
        }
        mirrorPortRegions = Ref.of(contextList);
    }

    @Override
    public Ref<MirrorType> getMirrorType() {
        return mirrorType;
    }

    @Override
    public Ref<? extends AclRuleCondition> getCondition() {
        return condition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AclRuleModificationJson that = (AclRuleModificationJson) o;
        return new EqualsBuilder()
            .append(ruleId, that.ruleId)
            .append(actionType, that.actionType)
            .append(forwardMirrorInterface, that.forwardMirrorInterface)
            .append(mirrorPortRegions, that.mirrorPortRegions)
            .append(mirrorType, that.mirrorType)
            .append(condition, that.condition)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId, actionType, forwardMirrorInterface, mirrorPortRegions, mirrorType, condition);
    }

    public static class AclRuleConditionImpl implements AclRuleCondition {
        @JsonSetter(value = "IPSource", nulls = AS_EMPTY)
        private Ref<AclRuleConditionIpImpl> ipSource = unassigned();

        @JsonSetter(value = "IPDestination", nulls = AS_EMPTY)
        private Ref<AclRuleConditionIpImpl> ipDestination = unassigned();

        @JsonSetter(value = "MACSource", nulls = AS_EMPTY)
        private Ref<AclRuleConditionMacAddressImpl> macSource = unassigned();

        @JsonSetter(value = "MACDestination", nulls = AS_EMPTY)
        private Ref<AclRuleConditionMacAddressImpl> macDestination = unassigned();

        @JsonSetter(value = "VLANId", nulls = AS_EMPTY)
        private Ref<AclRuleConditionIdImpl> vlanId = unassigned();

        @JsonSetter(value = "L4SourcePort", nulls = AS_EMPTY)
        private Ref<AclRuleConditionPortImpl> l4SourcePort = unassigned();

        @JsonSetter(value = "L4DestinationPort", nulls = AS_EMPTY)
        private Ref<AclRuleConditionPortImpl> l4DestinationPort = unassigned();

        @JsonSetter(value = "L4Protocol", nulls = AS_EMPTY)
        private Ref<Integer> l4Protocol = unassigned();

        @Override
        public Ref<? extends AclRuleConditionIp> getIpSource() {
            return ipSource;
        }

        @Override
        public Ref<? extends AclRuleConditionIp> getIpDestination() {
            return ipDestination;
        }

        @Override
        public Ref<? extends AclRuleConditionMacAddress> getMacSource() {
            return macSource;
        }

        @Override
        public Ref<? extends AclRuleConditionMacAddress> getMacDestination() {
            return macDestination;
        }

        @Override
        public Ref<? extends AclRuleConditionId> getVlanId() {
            return vlanId;
        }

        @Override
        public Ref<? extends AclRuleConditionPort> getL4SourcePort() {
            return l4SourcePort;
        }

        @Override
        public Ref<? extends AclRuleConditionPort> getL4DestinationPort() {
            return l4DestinationPort;
        }

        @Override
        public Ref<Integer> getL4Protocol() {
            return l4Protocol;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AclRuleConditionImpl that = (AclRuleConditionImpl) o;

            return new EqualsBuilder()
                .append(ipSource, that.ipSource)
                .append(ipDestination, that.ipDestination)
                .append(macSource, that.macSource)
                .append(macDestination, that.macDestination)
                .append(vlanId, that.vlanId)
                .append(l4SourcePort, that.l4SourcePort)
                .append(l4DestinationPort, that.l4DestinationPort)
                .append(l4Protocol, that.l4Protocol)
                .isEquals();
        }

        @Override
        public int hashCode() {
            return Objects.hash(ipSource, ipDestination, macSource, macDestination, vlanId, l4SourcePort, l4DestinationPort, l4Protocol);
        }
    }

    public static class AclRuleConditionIpImpl implements AclRuleConditionIp {
        @JsonSetter(value = "IPv4Address", nulls = AS_EMPTY)
        private Ref<String> ipv4Address = unassigned();

        @JsonSetter(value = "Mask", nulls = AS_EMPTY)
        private Ref<String> mask = unassigned();

        @Override
        public Ref<String> getIpv4Address() {
            return ipv4Address;
        }

        @Override
        public Ref<String> getMask() {
            return mask;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AclRuleConditionIp that = (AclRuleConditionIp) o;

            return new EqualsBuilder()
                .append(ipv4Address, that.getIpv4Address())
                .append(mask, that.getMask())
                .isEquals();
        }

        @Override
        public int hashCode() {
            return Objects.hash(ipv4Address, mask);
        }
    }

    public static class AclRuleConditionMacAddressImpl implements AclRuleConditionMacAddress {
        @JsonSetter(value = "MACAddress", nulls = AS_EMPTY)
        private Ref<String> macAddress = unassigned();

        @JsonSetter(value = "Mask", nulls = AS_EMPTY)
        private Ref<String> mask = unassigned();

        public Ref<String> getMacAddress() {
            return macAddress;
        }

        @Override
        public Ref<String> getMask() {
            return mask;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AclRuleConditionMacAddress that = (AclRuleConditionMacAddress) o;
            return new EqualsBuilder()
                .append(macAddress, that.getMacAddress())
                .append(mask, that.getMask())
                .isEquals();
        }

        @Override
        public int hashCode() {
            return Objects.hash(macAddress, mask);
        }
    }

    public static class AclRuleConditionIdImpl implements AclRuleConditionId {
        @JsonSetter(value = "Id", nulls = AS_EMPTY)
        private Ref<Long> id = unassigned();

        @JsonSetter(value = "Mask", nulls = AS_EMPTY)
        private Ref<Long> mask = unassigned();

        @Override
        public Ref<Long> getId() {
            return id;
        }

        @Override
        public Ref<Long> getMask() {
            return mask;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AclRuleConditionId that = (AclRuleConditionId) o;
            return new EqualsBuilder()
                .append(id, that.getId())
                .append(mask, that.getMask())
                .isEquals();
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, mask);
        }
    }

    public static class AclRuleConditionPortImpl implements AclRuleConditionPort {
        @JsonSetter(value = "Port", nulls = AS_EMPTY)
        private Ref<Long> port = unassigned();

        @JsonSetter(value = "Mask", nulls = AS_EMPTY)
        private Ref<Long> mask = unassigned();

        @Override
        public Ref<Long> getPort() {
            return port;
        }

        @Override
        public Ref<Long> getMask() {
            return mask;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AclRuleConditionPort that = (AclRuleConditionPort) o;
            return new EqualsBuilder()
                .append(port, that.getPort())
                .append(mask, that.getMask())
                .isEquals();
        }

        @Override
        public int hashCode() {
            return Objects.hash(port, mask);
        }
    }
}
