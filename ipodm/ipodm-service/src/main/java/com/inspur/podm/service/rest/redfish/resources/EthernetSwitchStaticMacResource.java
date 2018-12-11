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

package com.inspur.podm.service.rest.redfish.resources;

import static com.inspur.podm.service.rest.redfish.OptionsResponseBuilder.newOptionsForResourceBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.noContent;
import static javax.ws.rs.core.Response.ok;

import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.dto.EthernetSwitchStaticMacDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.inspur.podm.api.business.services.redfish.RemovalService;
import com.inspur.podm.api.business.services.redfish.UpdateService;
import com.inspur.podm.service.rest.redfish.json.templates.RedfishResourceAmazingWrapper;
import com.inspur.podm.service.rest.redfish.json.templates.actions.EthernetSwitchStaticMacJson;
import com.inspur.podm.service.rest.redfish.json.templates.actions.constraints.EthernetSwitchStaticMacConstraint;
import com.intel.podm.common.types.redfish.RedfishEthernetSwitchStaticMac;


@Produces(APPLICATION_JSON)
public class EthernetSwitchStaticMacResource extends BaseResource {
    @Inject
    private ReaderService<EthernetSwitchStaticMacDto> readerService;

    @Inject
    private RemovalService<EthernetSwitchStaticMacDto> removalService;

    @Inject
    private UpdateService<RedfishEthernetSwitchStaticMac> updateService;

    @GET
    @Override
    public RedfishResourceAmazingWrapper get() {
        Context context = getCurrentContext();
        EthernetSwitchStaticMacDto ethernetSwitchStaticMacDto = getOrThrow(() -> readerService.getResource(context));
        return new RedfishResourceAmazingWrapper(context, ethernetSwitchStaticMacDto);
    }

    @DELETE
    @Override
    public Response delete() throws TimeoutException, BusinessApiException {
        removalService.perform(getCurrentContext());
        return noContent().build();
    }

    @PATCH
    @Consumes(APPLICATION_JSON)
    public Response update(@EthernetSwitchStaticMacConstraint EthernetSwitchStaticMacJson json) throws BusinessApiException, TimeoutException {
        updateService.perform(getCurrentContext(), json);
        return ok(get()).build();
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceBuilder()
            .addDeleteMethod()
            .addPatchMethod()
            .build();
    }
}
