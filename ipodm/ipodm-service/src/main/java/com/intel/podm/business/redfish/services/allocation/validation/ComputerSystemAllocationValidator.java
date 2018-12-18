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

package com.intel.podm.business.redfish.services.allocation.validation;

import static java.util.Collections.emptySet;

import java.util.Set;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inspur.podm.api.business.Violations;
import com.inspur.podm.api.business.services.redfish.requests.RequestedNode;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.redfish.services.allocation.AllocationRequestProcessingException;

@Component
public class ComputerSystemAllocationValidator {
    @Autowired
    private ComputerSystemCollector computerSystemCollector;

    @Autowired
    private EthernetInterfacesValidator ethernetInterfacesValidator;

    @Autowired
    private SecurityAttributesValidator securityAttributesValidator;

    public Violations validate(RequestedNode requestedNode) {
        Violations violations = new Violations();
        violations.addAll(validateComputerSystemsAbilities(requestedNode));

        if (requestedNode.getName() == null) {
            violations.addViolation("Name of Composed Node cannot be null");
        }

        violations.addAll(ethernetInterfacesValidator.validate(requestedNode.getEthernetInterfaces()));
        violations.addAll(securityAttributesValidator.validate(requestedNode.getSecurity()));

        return violations;
    }

    private Violations validateComputerSystemsAbilities(RequestedNode requestedNode) {
        Violations violations = new Violations();

        Set<ComputerSystem> computerSystems = emptySet();
        try {
            computerSystems = computerSystemCollector.collectDistinctComputerSystemsFromResourceContexts(requestedNode);
        } catch (AllocationRequestProcessingException e) {
            violations.addAll(e.getViolations());
        }

        if (!canBeRealizedBySingleComputerSystem(computerSystems)) {
            violations.addViolation("Allocation of assets on multiple computer systems is not supported");
        }

        try {
            computerSystemCollector.collectCommonComputerSystemsFromChassisContexts(requestedNode);
        } catch (AllocationRequestProcessingException e) {
            violations.addAll(e.getViolations());
        }

        return violations;
    }

    private boolean canBeRealizedBySingleComputerSystem(Set<ComputerSystem> computerSystems) {
        return computerSystems.size() <= 1;
    }
}
