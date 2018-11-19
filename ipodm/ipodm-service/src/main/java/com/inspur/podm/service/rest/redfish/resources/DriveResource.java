/*
 * Copyright (c) 2016-2018 inspur Corporation
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

package com.inspur.podm.service.rest.redfish.resources;

import static com.inspur.podm.common.intel.types.redfish.ResourceNames.DRIVE_METRICS_RESOURCE_NAME;
import static com.inspur.podm.service.rest.redfish.OptionsResponseBuilder.newOptionsForResourceBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.ok;

import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.dto.DriveDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.inspur.podm.api.business.services.redfish.UpdateService;
import com.inspur.podm.common.intel.types.redfish.RedfishDrive;
import com.inspur.podm.service.rest.redfish.json.templates.RedfishResourceAmazingWrapper;
import com.inspur.podm.service.rest.redfish.json.templates.actions.DrivePartialRepresentation;
import com.inspur.podm.service.rest.redfish.json.templates.actions.constraints.DriveConstraint;


@Produces(APPLICATION_JSON)
@SuppressWarnings("checkstyle:ClassFanOutComplexity")
public class DriveResource extends BaseResource {
    @Inject
    private ReaderService<DriveDto> readerService;

    @Inject
    private UpdateService<RedfishDrive> updateService;

    @Override
    public RedfishResourceAmazingWrapper get() {
        Context context = getCurrentContext();
        DriveDto driveDto = getOrThrow(() -> readerService.getResource(context));
        return new RedfishResourceAmazingWrapper(context, driveDto);
    }

    @PATCH
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response updateDrive(@DriveConstraint DrivePartialRepresentation drivePartialRepresentation) throws TimeoutException, BusinessApiException {
        updateService.perform(getCurrentContext(), drivePartialRepresentation);
        return ok(get()).build();
    }

    @Path("Actions")
    public PcieDriveActionsResource getPcieDriveActionsResource() {
        return getResource(PcieDriveActionsResource.class);
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceBuilder()
            .addPatchMethod()
            .build();
    }

    @Path(DRIVE_METRICS_RESOURCE_NAME)
    public DriveMetricsResource getDriveMetrics() {
        return getResource(DriveMetricsResource.class);
    }
}
