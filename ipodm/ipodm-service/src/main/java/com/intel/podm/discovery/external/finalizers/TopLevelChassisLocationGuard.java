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

package com.intel.podm.discovery.external.finalizers;


//import javax.enterprise.context.Dependent;
//import javax.inject.Inject;
//import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.dao.ChassisDao;
import com.intel.podm.business.entities.redfish.Chassis;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.intel.podm.common.types.ChassisType.RACK;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
//import static javax.transaction.Transactional.TxType.MANDATORY;

//@Dependent
@Component
class TopLevelChassisLocationGuard {
    @Autowired
    ChassisDao chassisDao;

    private static final Logger logger = LoggerFactory.getLogger(TopLevelChassisLocationGuard.class);

    /**
     * This method unlinks top level chassis from racks which
     * have different location value. If rack is useless
     * (not managed by RMM and with empty children collection)
     * it is being removed.
     */
//    @Transactional(MANDATORY)
//    @TimeMeasured(tag = "[Finalizer]")
    @Transactional(propagation = Propagation.MANDATORY)
    public void assureSingleRackParent(Collection<Chassis> discoveredChassis) {
        List<Chassis> racks = chassisDao.getAllByChassisType(RACK);
        for (Chassis chassis : discoveredChassis) {
            logger.trace("Verifying chassis({}) location", chassis);
            unlinkFromNonParentRacks(chassis, racks);
        }
        deleteUselessRacks();
    }

    private void unlinkFromNonParentRacks(Chassis topLevelChassis, List<Chassis> racks) {
        racks.stream()
            .filter(rackChassis -> rackChassis.getContainedChassis().contains(topLevelChassis))
            .filter(rackChassis -> !Objects.equals(rackChassis.getLocationId(), topLevelChassis.getLocationParentId()))
            .forEach(rackChassis -> rackChassis.unlinkContainedChassis(topLevelChassis));
    }

    private void deleteUselessRacks() {
        List<Chassis> racksToRemove = chassisDao.getAllByChassisType(RACK).stream()
            .filter(rack -> rack.getContainedChassis().isEmpty())
            .filter(rack -> isNull(rack.getService()))
            .collect(toList());

        racksToRemove.forEach(chassisDao::remove);
    }
}
