/*
 * Copyright (c) 2016-2018 Intel Corporation
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

import static com.intel.podm.common.types.ChassisType.POD;
import static com.intel.podm.common.types.ChassisType.RACK;
import static com.intel.podm.common.utils.IterableHelper.single;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.intel.podm.business.entities.dao.ChassisDao;
import com.intel.podm.business.entities.redfish.Chassis;
//import com.intel.podm.common.enterprise.utils.logger.TimeMeasured;
import com.intel.podm.discovery.external.finder.RackChassisFinder;

//@Dependent
@Component
@Lazy
public class DrawerChassisLinker {
    @Autowired
    private ChassisDao chassisDao;

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(DrawerChassisLinker.class);

    @Autowired
    private RackChassisFinder rackFinder;

//    @TimeMeasured(tag = "[Finalizer]")
//    @Transactional(MANDATORY)
    @org.springframework.transaction.annotation.Transactional
    public void linkToModel(Collection<Chassis> topLevelChassis) {
        linkToPod(topLevelChassis.stream().filter(this::shouldBeLinkedToPod));
        linkToRack(topLevelChassis.stream().filter(chassis -> !shouldBeLinkedToPod(chassis)));
    }

    private void linkToPod(Stream<Chassis> chassisStream) {
        Chassis podChassis = single(chassisDao.getAllByChassisType(POD));
        chassisStream
            .peek(chassis -> logger.trace("Linking chassis {}:{} ", chassis.getChassisType(), chassis.getId()))
            .forEach(podChassis::addContainedChassis);
    }

    private void linkToRack(Stream<Chassis> chassisForRackLinking) {
        RackCache rackCache = new RackCache(rackFinder);

        chassisForRackLinking
            .filter(chassis -> !isAlreadyLinkedToProperRack(chassis))
            .forEach(chassis -> {
                unlinkFromInvalidParent(chassis);
                rackCache.findAnyOrCreate(chassis.getLocationParentId()).addContainedChassis(chassis);
            });
    }

    private void unlinkFromInvalidParent(Chassis chassis) {
        Chassis parent = chassis.getContainedByChassis();
        boolean chassisIsUnderInvalidParent = parent != null
            && !Objects.equals(parent.getLocationId(), chassis.getLocationParentId());

        if (chassisIsUnderInvalidParent) {
            parent.unlinkContainedChassis(chassis);
        }
    }

    private boolean shouldBeLinkedToPod(Chassis chassis) {
        return chassis.getLocationParentId() == null || chassis.is(RACK);
    }

    private boolean isAlreadyLinkedToProperRack(Chassis chassis) {
        Chassis parent = chassis.getContainedByChassis();
        return parent != null
            && parent.is(RACK)
            && Objects.equals(parent.getLocationId(), chassis.getLocationParentId());
    }

    static class RackCache {
        private Map<String, Chassis> cache = new HashMap<>();

        private RackChassisFinder rackFinder;

        RackCache(RackChassisFinder rackFinder) {
            this.rackFinder = rackFinder;
        }

        Chassis findAnyOrCreate(String locationId) {
            return cache.computeIfAbsent(locationId, s -> rackFinder.findAnyOrCreate(locationId));
        }
    }
}
