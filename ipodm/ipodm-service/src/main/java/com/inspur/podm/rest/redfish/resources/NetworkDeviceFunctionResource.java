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

package com.inspur.podm.rest.redfish.resources;

import com.inspur.podm.business.BusinessApiException;
import com.inspur.podm.business.dto.NetworkDeviceFunctionDto;
import com.inspur.podm.business.services.context.Context;
import com.inspur.podm.business.services.redfish.ReaderService;
import com.inspur.podm.business.services.redfish.UpdateService;
import com.inspur.podm.common.types.redfish.RedfishNetworkDeviceFunction;
import com.inspur.podm.rest.redfish.json.templates.RedfishResourceAmazingWrapper;
import com.inspur.podm.rest.redfish.json.templates.actions.NetworkDeviceFunctionPartialRepresentation;
import com.inspur.podm.rest.redfish.json.templates.actions.constraints.NetworkDeviceFunctionConstraint;


import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.PATCH;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeoutException;

import static com.inspur.podm.rest.redfish.OptionsResponseBuilder.newOptionsForResourceBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.ok;


@Produces(APPLICATION_JSON)
public class NetworkDeviceFunctionResource extends BaseResource {
    @Inject
    private ReaderService<NetworkDeviceFunctionDto> readerService;

    @Inject
    private UpdateService<RedfishNetworkDeviceFunction> updateService;

    @Override
    public RedfishResourceAmazingWrapper get() {
        Context context = getCurrentContext();
        NetworkDeviceFunctionDto networkDeviceFunctionDto = getOrThrow(() -> readerService.getResource(context));
        return new RedfishResourceAmazingWrapper(context, networkDeviceFunctionDto);
    }

    @PATCH
    @Consumes(APPLICATION_JSON)
    public Response updateNetworkDeviceFunction(@NetworkDeviceFunctionConstraint NetworkDeviceFunctionPartialRepresentation updateNetworkDeviceActionJson)
        throws TimeoutException, BusinessApiException {

        updateService.perform(getCurrentContext(), updateNetworkDeviceActionJson);
        return ok(get()).build();
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceBuilder()
            .addPatchMethod()
            .build();
    }
}
