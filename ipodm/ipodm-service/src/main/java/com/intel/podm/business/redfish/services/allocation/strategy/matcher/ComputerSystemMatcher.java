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

package com.intel.podm.business.redfish.services.allocation.strategy.matcher;

import static com.inspur.podm.api.business.Violations.createWithViolations;
import static com.intel.podm.business.entities.redfish.base.StatusControl.statusOf;
import static java.util.Objects.isNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inspur.podm.api.business.services.redfish.requests.RequestedNode;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.redfish.services.allocation.AllocationRequestProcessingException;
import com.intel.podm.business.redfish.services.allocation.strategy.ResourceFinderException;
import com.intel.podm.business.redfish.services.allocation.validation.ComputerSystemCollector;

@Component
//@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:MethodCount"})
public class ComputerSystemMatcher {
    @Autowired
    private ProcessorMatcher processorMatcher;
    @Autowired
    private MemoryMatcher memoryMatcher;
    @Autowired
    private LocalStorageMatcher localDriveMatcher;
    @Autowired
    private LocalStorageCollector localStorageCollector;
    @Autowired
    private EthernetInterfaceMatcher ethernetInterfaceMatcher;
    @Autowired
    private ComputerSystemAttributesMatcher computerSystemAttributesMatcher;
    @Autowired
    private ComputerSystemCollector computerSystemCollector;
    @Autowired
    private ComputerSystemSecurityAttributesMatcher computerSystemSecurityAttributesMatcher;
    
    private static final Logger logger = LoggerFactory.getLogger(ComputerSystemMatcher.class);

    public Collection<ComputerSystem> matches(RequestedNode requestedNode, Collection<ComputerSystem> computerSystems) throws ResourceFinderException {
        FilteringCollection<ComputerSystem> computerSystemList = new FilteringCollection<>(computerSystems);
        computerSystemList.filter(byEnabledAndHealthy(), "status")
            .filter(byComputerSystem(getComputerSystemByResourceContexts(requestedNode)), "resource")
            .filter(byComputerSystemsFromChassis(getCommonComputerSystemsByChassisContexts(requestedNode)), "chassis")
            .filter(byProcessors(requestedNode), "processors")
            .filter(byMemoryModules(requestedNode), "memory")
            .filter(byLocalDrives(requestedNode), "local drives")
            .filter(byEthernetInterfaces(requestedNode), "ethernet interfaces")
            .filter(byComputerSystemAttributes(requestedNode), "computer system attributes")
            .filter(byComputerSystemSecurityAttributes(requestedNode), "security");

        String msg = computerSystemList.getFilterStatistics();
        logger.info(msg);

        Collection<ComputerSystem> systems = computerSystemList.getCollection();
        if (systems.isEmpty()) {
            throw new ResourceFinderException(createWithViolations("There are no computer systems available for this allocation request.", msg));
        }

        return systems;
    }

    private Predicate<ComputerSystem> byEnabledAndHealthy() {
        return computerSystem -> statusOf(computerSystem).isEnabled().isHealthy().verify();
    }

    private Predicate<ComputerSystem> byComputerSystem(ComputerSystem requestedComputerSystem) {
        return computerSystem -> isNull(requestedComputerSystem) || Objects.equals(requestedComputerSystem, computerSystem);
    }

    private Predicate<ComputerSystem> byComputerSystemsFromChassis(Set<ComputerSystem> requestedComputerSystems) {
        return computerSystem -> isEmpty(requestedComputerSystems) || requestedComputerSystems.contains(computerSystem);
    }

    private Predicate<ComputerSystem> byProcessors(RequestedNode requestedNode) {
        return computerSystem -> processorMatcher.matches(requestedNode, computerSystem.getProcessors());
    }

    private Predicate<ComputerSystem> byMemoryModules(RequestedNode requestedNode) {
        return computerSystem -> memoryMatcher.matches(requestedNode, computerSystem);
    }

    private Predicate<ComputerSystem> byLocalDrives(RequestedNode requestedNode) {
        return computerSystem -> localDriveMatcher.matches(requestedNode, localStorageCollector.getStorageUnderComputerSystem(computerSystem));
    }

    private Predicate<ComputerSystem> byEthernetInterfaces(RequestedNode requestedNode) {
        return computerSystem -> ethernetInterfaceMatcher.matches(requestedNode, computerSystem.getEthernetInterfaces());
    }

    private Predicate<ComputerSystem> byComputerSystemAttributes(RequestedNode requestedNode) {
        return computerSystem -> computerSystemAttributesMatcher.matches(requestedNode, computerSystem);
    }

    private Predicate<ComputerSystem> byComputerSystemSecurityAttributes(RequestedNode requestedNode) {
        return computerSystem -> computerSystemSecurityAttributesMatcher.matches(requestedNode.getSecurity(), computerSystem);
    }

    private ComputerSystem getComputerSystemByResourceContexts(RequestedNode requestedNode) {
        try {
            Set<ComputerSystem> computerSystems = computerSystemCollector.collectDistinctComputerSystemsFromResourceContexts(requestedNode);
            if (computerSystems.size() <= 1) {
                return computerSystems.isEmpty() ? null : computerSystems.iterator().next();
            }
        } catch (AllocationRequestProcessingException e) {
            throw new IllegalStateException("Some of provided resources are not valid", e);
        }

        throw new IllegalStateException("Allocation of assets on multiple computer systems is not supported");
    }

    private Set<ComputerSystem> getCommonComputerSystemsByChassisContexts(RequestedNode requestedNode) {
        try {
            return computerSystemCollector.collectCommonComputerSystemsFromChassisContexts(requestedNode);
        } catch (AllocationRequestProcessingException e) {
            throw new IllegalStateException("Some of provided resources are not valid", e);
        }
    }
}
