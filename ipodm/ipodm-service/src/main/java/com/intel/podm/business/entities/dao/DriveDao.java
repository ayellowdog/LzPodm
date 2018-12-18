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

package com.intel.podm.business.entities.dao;

import static com.intel.podm.business.entities.redfish.Drive.GET_PRIMARY_DRIVE;
import static com.intel.podm.common.types.PciePortType.DOWNSTREAM_PORT;
import static com.intel.podm.common.utils.IterableHelper.singleOrNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.redfish.Chassis;
import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.ConnectedEntity;
import com.intel.podm.business.entities.redfish.Drive;
import com.intel.podm.business.entities.redfish.Endpoint;
import com.intel.podm.business.entities.redfish.Port;
import com.intel.podm.business.entities.redfish.Switch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class DriveDao extends Dao<Drive> {
	
    private static final Logger logger = LoggerFactory.getLogger(DriveDao.class);

    @Autowired
    private PciePortDao pciePortDao;

    @Autowired
    private ChassisDao chassisDao;

    @Transactional(propagation = Propagation.MANDATORY)
    public Set<Drive> getAchievablePcieDrives(ComputerSystem computerSystem) {
        List<String> pcieConnectionIds = computerSystem.getPcieConnectionIds();

        return pciePortDao.getUpstreamPortsByCableIds(pcieConnectionIds).stream()
            .map(this::getAchievablePcieDrives)
            .flatMap(Collection::stream)
            .collect(toSet());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Set<Drive> getAchievablePcieDrives(Port upstreamPort) {
        Stream<Port> downstreamPorts = getDownstreamPortsThatBelongToTheSameSwitch(upstreamPort);
        return getConnectedDrives(downstreamPorts)
            .filter(pcieDrive -> !pcieDrive.getMetadata().isAllocated())
            .peek(pcieDrive -> logger.trace("Drive preselected after applying 'is already allocated' filter: {}", pcieDrive))
            .filter(Drive::isAvailable)
            .peek(pcieDrive -> logger.trace("Drive preselected after applying 'can be allocated' filter: {}", pcieDrive))
            .collect(toSet());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public List<Drive> findComplementaryDrives(Drive drive) {
        Chassis driveChassis = drive.getChassis();
        String driveDiscriminator = drive.getMultiSourceDiscriminator();
        return chassisDao.findComplementaryChassis(driveChassis).stream()
            .flatMap(chassis -> chassis.getDrives().stream())
            .filter(mappedDrive -> Objects.equals(mappedDrive.getMultiSourceDiscriminator(), driveDiscriminator))
            .distinct()
            .collect(toList());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public Optional<Drive> findPrimaryDrive(Drive complementaryDrive) {
        TypedQuery<Drive> query = entityManager.createNamedQuery(GET_PRIMARY_DRIVE, Drive.class);
        query.setParameter("multiSourceDiscriminator", complementaryDrive.getMultiSourceDiscriminator());
        return ofNullable(singleOrNull(query.getResultList()));
    }

    private Stream<Drive> getConnectedDrives(Stream<Port> downstreamPorts) {
        return downstreamPorts
            .map(Port::getEndpoints)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .map(Endpoint::getConnectedEntities)
            .flatMap(Collection::stream)
            .map(ConnectedEntity::getEntityLink)
            .filter(Drive.class::isInstance)
            .map(Drive.class::cast);
    }

    private Stream<Port> getDownstreamPortsThatBelongToTheSameSwitch(Port upstreamPort) {
        return Stream.of(upstreamPort)
            .map(Port::getSwitch)
            .map(Switch::getPorts)
            .flatMap(Collection::stream)
            .filter(pciePort -> Objects.equals(pciePort.getPortType(), DOWNSTREAM_PORT));
    }
}
