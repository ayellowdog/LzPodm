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

import static com.inspur.podm.service.rest.error.PodmExceptions.invalidHttpMethod;
import static com.inspur.podm.service.rest.redfish.OptionsResponseBuilder.newOptionsForResourceActionBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.noContent;

import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.services.redfish.ActionService;
import com.inspur.podm.api.business.services.redfish.requests.ChangeTpmStateRequest;
import com.inspur.podm.api.business.services.redfish.requests.StartDeepDiscoveryRequest;
import com.inspur.podm.service.rest.redfish.json.templates.actions.ChangeTpmStateActionJson;


@Produces(APPLICATION_JSON)
public class ComputerSystemActionsResource extends BaseResource {
    @Inject
    private ActionService<StartDeepDiscoveryRequest> startDeepDiscoveryRequestActionService;

    @Inject
    private ActionService<ChangeTpmStateRequest> changeTpmStateRequestActionService;

    @Override
    public Object get() {
        throw invalidHttpMethod();
    }

    @Path("ComputerSystem.Reset")
    public ResetActionResource reset() throws TimeoutException, BusinessApiException {
        return getResource(ResetActionResource.class);
    }

    @POST
    @Path("Oem/inspur.Oem.StartDeepDiscovery")
    public Response startDeepDiscovery() throws TimeoutException, BusinessApiException {
        startDeepDiscoveryRequestActionService.perform(getCurrentContext(), null);
        return noContent().build();
    }

    @POST
    @Path("Oem/inspur.Oem.ChangeTPMState")
    public Response changeTpmState(ChangeTpmStateActionJson request) throws TimeoutException, BusinessApiException {
        changeTpmStateRequestActionService.perform(getCurrentContext(), request);
        return noContent().build();
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceActionBuilder().build();
    }
}
