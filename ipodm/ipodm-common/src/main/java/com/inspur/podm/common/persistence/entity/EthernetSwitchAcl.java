/*
 * Copyright (c) 2017-2018 Intel Corporation
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

package com.inspur.podm.common.persistence.entity;


import static com.inspur.podm.common.utils.Contracts.requiresNonNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import com.inspur.podm.common.intel.types.Id;
import com.inspur.podm.common.persistence.BaseEntity;

//@javax.persistence.Entity
//@Table(name = "ethernet_switch_acl", indexes = @Index(name = "idx_ethernet_switch_acl_entity_id", columnList = "entity_id", unique = true))
//@Eventable
//@SuppressWarnings("checkstyle:MethodCount")
public class EthernetSwitchAcl extends DiscoverableEntity {
/** @Fields serialVersionUID: TODO 功能描述  */
	private static final long serialVersionUID = -6264410326686356028L;

	//    @Column(name = "entity_id", columnDefinition = ENTITY_ID_STRING_COLUMN_DEFINITION)
    private Id entityId;

//    @SuppressEvents
//    @OneToMany(mappedBy = "ethernetSwitchAcl", fetch = LAZY, cascade = {MERGE, PERSIST})
    private Set<EthernetSwitchAclRule> rules = new HashSet<>();

//    @ManyToMany(fetch = LAZY, cascade = {MERGE, PERSIST})
//    @JoinTable(
//        name = "ethernet_switch_acl_bound_port",
//        joinColumns = {@JoinColumn(name = "acl_id", referencedColumnName = "id")},
//        inverseJoinColumns = {@JoinColumn(name = "port_id", referencedColumnName = "id")}
//    )
    private Set<EthernetSwitchPort> boundPorts = new HashSet<>();

//    @ManyToMany(fetch = LAZY, cascade = {MERGE, PERSIST})
//    @JoinTable(
//        name = "ethernet_switch_acl_bind_action_allowable_value_port",
//        joinColumns = {@JoinColumn(name = "acl_id", referencedColumnName = "id")},
//        inverseJoinColumns = {@JoinColumn(name = "port_id", referencedColumnName = "id")}
//    )
    private Set<EthernetSwitchPort> bindActionAllowableValues = new HashSet<>();

//    @ManyToOne(fetch = LAZY, cascade = {MERGE, PERSIST})
//    @JoinColumn(name = "ethernet_switch_id")
    private EthernetSwitch ethernetSwitch;

    @Override
    public Id getId() {
        return entityId;
    }

    @Override
    public void setId(Id id) {
        entityId = id;
    }

    public Set<EthernetSwitchAclRule> getRules() {
        return rules;
    }

    public void addRule(EthernetSwitchAclRule ethernetSwitchAclRule) {
        requiresNonNull(ethernetSwitchAclRule, "ethernetSwitchAclRule");

        rules.add(ethernetSwitchAclRule);
        if (!this.equals(ethernetSwitchAclRule.getEthernetSwitchAcl())) {
            ethernetSwitchAclRule.setEthernetSwitchAcl(this);
        }
    }

    public void unlinkRule(EthernetSwitchAclRule ethernetSwitchAclRule) {
        if (rules.contains(ethernetSwitchAclRule)) {
            rules.remove(ethernetSwitchAclRule);
            if (ethernetSwitchAclRule != null) {
                ethernetSwitchAclRule.unlinkEthernetSwitchAcl(this);
            }
        }
    }

    public Set<EthernetSwitchPort> getBoundPorts() {
        return boundPorts;
    }

    public void addBoundPort(EthernetSwitchPort ethernetSwitchPort) {
        requiresNonNull(ethernetSwitchPort, "ethernetSwitchPort");

        boundPorts.add(ethernetSwitchPort);
        if (!ethernetSwitchPort.getActiveAcls().contains(this)) {
            ethernetSwitchPort.addActiveAcl(this);
        }
    }

    public void unlinkBoundPort(EthernetSwitchPort ethernetSwitchPort) {
        if (boundPorts.contains(ethernetSwitchPort)) {
            boundPorts.remove(ethernetSwitchPort);
            if (ethernetSwitchPort != null) {
                ethernetSwitchPort.unlinkActiveAcl(this);
            }
        }
    }

    public void uncoupleBoundPorts(Predicate<EthernetSwitchPort> unlinkPredicate) {
        unlinkCollection(boundPorts, this::unlinkBoundPort, unlinkPredicate);
    }

    public Set<EthernetSwitchPort> getBindActionAllowableValues() {
        return bindActionAllowableValues;
    }

    public void addBindActionAllowableValue(EthernetSwitchPort ethernetSwitchPort) {
        requiresNonNull(ethernetSwitchPort, "ethernetSwitchPort");

        bindActionAllowableValues.add(ethernetSwitchPort);
        if (!ethernetSwitchPort.getAclsToBind().contains(this)) {
            ethernetSwitchPort.addAclsToBind(this);
        }
    }

    public void unlinkBindActionAllowableValue(EthernetSwitchPort ethernetSwitchPort) {
        if (bindActionAllowableValues.contains(ethernetSwitchPort)) {
            bindActionAllowableValues.remove(ethernetSwitchPort);
            if (ethernetSwitchPort != null) {
                ethernetSwitchPort.unlinkAclsToBind(this);
            }
        }
    }

    public void uncoupleBindActionAllowableValues(Predicate<EthernetSwitchPort> unlinkPredicate) {
        unlinkCollection(bindActionAllowableValues, this::unlinkBindActionAllowableValue, unlinkPredicate);
    }

    public EthernetSwitch getEthernetSwitch() {
        return ethernetSwitch;
    }

    public void setEthernetSwitch(EthernetSwitch ethernetSwitch) {
        if (!Objects.equals(this.ethernetSwitch, ethernetSwitch)) {
            unlinkEthernetSwitch(this.ethernetSwitch);
            this.ethernetSwitch = ethernetSwitch;
            if (ethernetSwitch != null && !ethernetSwitch.getAcls().contains(this)) {
                ethernetSwitch.addAcl(this);
            }
        }
    }

    public void unlinkEthernetSwitch(EthernetSwitch ethernetSwitch) {
        if (Objects.equals(this.ethernetSwitch, ethernetSwitch)) {
            this.ethernetSwitch = null;
            if (ethernetSwitch != null) {
                ethernetSwitch.unlinkAcl(this);
            }
        }
    }

    @Override
    public void preRemove() {
        unlinkCollection(rules, this::unlinkRule);
        unlinkCollection(boundPorts, this::unlinkBoundPort);
        unlinkCollection(bindActionAllowableValues, this::unlinkBindActionAllowableValue);
        unlinkEthernetSwitch(ethernetSwitch);
    }

    @Override
    public boolean containedBy(BaseEntity possibleParent) {
        return isContainedBy(possibleParent, ethernetSwitch);
    }
}
