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

package com.inspur.podm.rest.redfish.resources;

import com.inspur.podm.business.BusinessApiException;
import com.inspur.podm.business.dto.VlanNetworkInterfaceDto;
import com.inspur.podm.business.services.context.Context;
import com.inspur.podm.business.services.redfish.ReaderService;
import com.inspur.podm.business.services.redfish.RemovalService;
import com.inspur.podm.business.services.redfish.UpdateService;
import com.inspur.podm.common.types.redfish.RedfishVlanNetworkInterface;
import com.inspur.podm.rest.redfish.OptionsResponseBuilder;
import com.inspur.podm.rest.redfish.json.templates.RedfishResourceAmazingWrapper;
import com.inspur.podm.rest.redfish.json.templates.actions.UpdateVlanJson;
import com.inspur.podm.rest.redfish.json.templates.actions.constraints.VlanConstraint;


import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeoutException;

import static com.inspur.podm.business.services.context.ContextType.ETHERNET_SWITCH_PORT;
import static com.inspur.podm.rest.redfish.OptionsResponseBuilder.newOptionsForResourceBuilder;
import static com.inspur.podm.rest.error.PodmExceptions.invalidHttpMethod;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.noContent;
import static javax.ws.rs.core.Response.ok;


@Produces(APPLICATION_JSON)
@SuppressWarnings({"checkstyle:ClassFanOutComplexity"})
public class VlanNetworkInterfaceResource extends BaseResource {
    @Inject
    private ReaderService<VlanNetworkInterfaceDto> readerService;

    @Inject
    private RemovalService<RedfishVlanNetworkInterface> removalService;

    @Inject
    private UpdateService<RedfishVlanNetworkInterface> vlanUpdateService;

    @GET
    @Override
    public RedfishResourceAmazingWrapper get() {
        Context context = getCurrentContext();
        VlanNetworkInterfaceDto interfaceDto = getOrThrow(() -> readerService.getResource(context));
        return new RedfishResourceAmazingWrapper(context, interfaceDto);
    }

    @PATCH
    @Produces(APPLICATION_JSON)
    public Response patchVlan(@VlanConstraint UpdateVlanJson updateVlanJson) throws BusinessApiException, TimeoutException {
        Context currentContext = getCurrentContext();

        if (!isPatchEnabled(currentContext)) {
            throw invalidHttpMethod("The specified vlan cannot be updated");
        }

        vlanUpdateService.perform(getCurrentContext(), updateVlanJson);
        return ok(get()).build();
    }

    @DELETE
    public Response deleteVlan() throws TimeoutException, BusinessApiException {
        Context currentContext = getCurrentContext();

        if (!isDeleteEnabled(currentContext)) {
            throw invalidHttpMethod("The specified vlan cannot be deleted");
        }

        removalService.perform(currentContext);
        return noContent().build();
    }

    private boolean isPatchEnabled(Context currentContext) {
        return ETHERNET_SWITCH_PORT.equals(currentContext.getParent().getType());
    }

    private boolean isDeleteEnabled(Context currentContext) {
        return ETHERNET_SWITCH_PORT.equals(currentContext.getParent().getType());
    }

    @Override
    protected Response createOptionsResponse() {
        OptionsResponseBuilder optionsResponseBuilder = newOptionsForResourceBuilder();

        if (isPatchEnabled(getCurrentContext())) {
            optionsResponseBuilder.addPatchMethod();
        }

        if (isDeleteEnabled(getCurrentContext())) {
            optionsResponseBuilder.addDeleteMethod();
        }

        return optionsResponseBuilder
            .build();
    }
}
