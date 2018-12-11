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

import static com.inspur.podm.api.business.services.context.SingletonContext.singletonContextOf;
import static com.inspur.podm.service.rest.error.PodmExceptions.invalidHttpMethod;
import static com.inspur.podm.service.rest.redfish.OptionsResponseBuilder.newOptionsForResourceActionBuilder;
import static com.intel.podm.common.types.actions.ActionInfoNames.ATTACH_RESOURCE_ACTION_INFO;
import static com.intel.podm.common.types.actions.ActionInfoNames.DETACH_RESOURCE_ACTION_INFO;
import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.noContent;

import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.dto.actions.actionInfo.ActionInfoDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.ActionService;
import com.inspur.podm.api.business.services.redfish.requests.AssemblyRequest;
import com.inspur.podm.api.business.services.redfish.requests.AttachResourceRequest;
import com.inspur.podm.api.business.services.redfish.requests.DetachResourceRequest;
import com.inspur.podm.api.business.services.redfish.requests.ResetRequest;
import com.inspur.podm.service.rest.redfish.json.templates.RedfishResourceAmazingWrapper;
import com.inspur.podm.service.rest.redfish.json.templates.actions.AttachResourceJson;
import com.inspur.podm.service.rest.redfish.json.templates.actions.DetachResourceJson;
import com.inspur.podm.service.rest.redfish.json.templates.actions.ResetActionJson;


@Produces(APPLICATION_JSON)
public class ComposedNodeActionsResource extends BaseResource {
    @Inject
    private ActionService<ResetRequest> resetRequestActionService;

    @Inject
    private ActionService<AssemblyRequest> assemblyRequestActionService;

    @Inject
    private ActionService<AttachResourceRequest> attachResourceRequestActionService;

    @Inject
    private ActionService<DetachResourceRequest> detachResourceRequestActionService;

    @Override
    public Object get() {
        throw invalidHttpMethod();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Path("ComposedNode.Reset")
    public Response reset(ResetActionJson resetActionJson) throws TimeoutException, BusinessApiException {
        resetRequestActionService.perform(getCurrentContext(), resetActionJson);
        return noContent().build();
    }

    @POST
    @Path("ComposedNode.Assemble")
    public Response assemble() throws TimeoutException, BusinessApiException {
        assemblyRequestActionService.perform(getCurrentContext(), null);
        return noContent().build();
    }

    @POST
    @Path("ComposedNode.AttachResource")
    public Response attachEndpoint(AttachResourceJson attachResourceJson) throws TimeoutException, BusinessApiException {
        attachResourceRequestActionService.perform(getCurrentContext(), attachResourceJson);
        return noContent().build();
    }

    @POST
    @Path("ComposedNode.DetachResource")
    public Response detachResource(DetachResourceJson detachResourceJson) throws TimeoutException, BusinessApiException {
        detachResourceRequestActionService.perform(getCurrentContext(), detachResourceJson);
        return noContent().build();
    }

    @GET
    @Path(ATTACH_RESOURCE_ACTION_INFO)
    public RedfishResourceAmazingWrapper getAttachResourceActionInfo() throws BusinessApiException {
        Context context = getCurrentContext();
        ActionInfoDto actionInfoDto = attachResourceRequestActionService.getActionInfo(context);
        return new RedfishResourceAmazingWrapper(singletonContextOf(context, format("Actions/%s", ATTACH_RESOURCE_ACTION_INFO)), actionInfoDto);
    }

    @GET
    @Path(DETACH_RESOURCE_ACTION_INFO)
    public RedfishResourceAmazingWrapper getDetachResourceActionInfo() throws BusinessApiException {
        Context context = getCurrentContext();
        ActionInfoDto actionInfoDto = detachResourceRequestActionService.getActionInfo(context);
        return new RedfishResourceAmazingWrapper(singletonContextOf(context, format("Actions/%s", DETACH_RESOURCE_ACTION_INFO)), actionInfoDto);
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceActionBuilder().build();
    }
}
