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

package com.intel.podm.business.redfish.services.attach.strategy;

import static com.inspur.podm.api.business.Violations.createWithViolations;
import static com.inspur.podm.api.business.services.context.ContextType.DRIVE;

import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.RequestValidationException;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.context.ContextType;
import com.inspur.podm.api.business.services.redfish.requests.AttachResourceRequest;
import com.intel.podm.business.entities.dao.DriveDao;
import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.Drive;
import com.intel.podm.business.redfish.services.EntityTreeTraverser;
import com.intel.podm.business.redfish.services.PcieDriveAttacher;
import com.intel.podm.business.redfish.services.PcieZoneAttacher;
import com.intel.podm.business.redfish.services.ServiceTraverser;
import com.intel.podm.business.redfish.services.attach.AttachResourceStrategy;
import com.intel.podm.common.synchronization.TaskCoordinator;
import com.intel.podm.config.base.DynamicHolder;
import com.intel.podm.config.base.dto.ServiceConfig;

@Component
public class PcieDriveAttachStrategy implements AttachResourceStrategy {
    @Autowired
    private ServiceTraverser traverser;

    @Autowired
    private EntityTreeTraverser entityTraverser;

    @Autowired
    private TaskCoordinator taskCoordinator;

    @Autowired
//    @Config
    private DynamicHolder<ServiceConfig> serviceConfigHolder;

    @Autowired
    private PcieZoneAttacher zoneAttacher;

    @Autowired
    private PcieDriveAttacher driveAttacher;

    @Autowired
    private DriveDao driveDao;

    @Override
    public ContextType supportedType() {
        return DRIVE;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void attach(Context target, Context resourceContext) throws BusinessApiException, TimeoutException {
        taskCoordinator.run(traverser.traverseServiceUuid(resourceContext), () -> zoneAttacher.attachEndpointToZone(target, resourceContext));
        taskCoordinator.run(serviceConfigHolder.get(ServiceConfig.class).getUuid(), () -> driveAttacher.attachPcieDriveToNode(target, resourceContext));
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void validate(Context target, AttachResourceRequest request) throws BusinessApiException {
        Drive drive = (Drive) entityTraverser.traverse(request.getResourceContext());
        ComposedNode composedNode = (ComposedNode) entityTraverser.traverse(target);
        validateDriveAchievable(drive, composedNode);
        validateDriveAttachAbility(drive);
    }

    private void validateDriveAchievable(Drive drive, ComposedNode composedNode) throws RequestValidationException {
        Set<Drive> achievableDrives = driveDao.getAchievablePcieDrives(composedNode.getComputerSystem());
        if (!achievableDrives.contains(drive)) {
            throw new RequestValidationException(createWithViolations("Selected Drive is not achievable for this node!"));
        }
    }

    private void validateDriveAttachAbility(Drive drive) throws RequestValidationException {
        if (drive.getMetadata().isAllocated()) {
            throw new RequestValidationException(createWithViolations("Selected Drive is currently in use!"));
        }
    }
}
