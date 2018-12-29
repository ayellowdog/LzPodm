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

package com.intel.podm.discovery.external.finalizers;


import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.redfish.Chassis;
import com.intel.podm.business.entities.redfish.ExternalService;

//@ApplicationScoped
@Component
public class ChassisHierarchyMaintainer {
    private static final Predicate<Chassis> PARENT_DOES_NOT_EXIST = chassis -> chassis.getContainedByChassis() == null;

    private static final Predicate<Chassis> PARENT_POINTS_TO_DIFFERENT_SERVICE = chassis -> {
        Chassis parentChassis = chassis.getContainedByChassis();
        List<ExternalService> parentChassisOrigin = parentChassis.getExternalServices();
        List<ExternalService> chassisOrigin = chassis.getExternalServices();
        return chassisOrigin.stream().noneMatch(parentChassisOrigin::contains);
    };

    @Autowired
    private DrawerChassisLinker chassisLinker;

    @Autowired
    private TopLevelChassisLocationGuard topLevelChassisLocationGuard;

//    @Transactional(MANDATORY)
    @Transactional(propagation = Propagation.MANDATORY)
    public void maintain(Collection<Chassis> discoveredChassis) {
        Collection<Chassis> topLevelChassis = getTopLevelChassis(discoveredChassis);
        chassisLinker.linkToModel(topLevelChassis);
        topLevelChassisLocationGuard.assureSingleRackParent(topLevelChassis);
    }

    Collection<Chassis> getTopLevelChassis(Collection<Chassis> discoveredChassis) {
        return discoveredChassis.stream().filter(PARENT_DOES_NOT_EXIST.or(PARENT_POINTS_TO_DIFFERENT_SERVICE)).collect(toSet());
    }
}
