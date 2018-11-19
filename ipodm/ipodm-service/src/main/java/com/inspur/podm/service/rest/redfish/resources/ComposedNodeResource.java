/*
 * Copyright (c) 2015-2018 inspur Corporation
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
import javax.inject.Named;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.dto.ComposedNodeDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.inspur.podm.api.business.services.redfish.RemovalService;
import com.inspur.podm.api.business.services.redfish.UpdateService;
import com.inspur.podm.common.intel.types.redfish.RedfishComputerSystem;
import com.inspur.podm.service.rest.redfish.json.templates.RedfishResourceAmazingWrapper;
import com.inspur.podm.service.rest.redfish.json.templates.actions.ComposedNodePartialRepresentation;
import com.inspur.podm.service.rest.redfish.json.templates.actions.constraints.ComposedNodeConstraint;


@Produces(APPLICATION_JSON)
public class ComposedNodeResource extends BaseResource {
    @Inject
    private ReaderService<ComposedNodeDto> readerService;
    @Inject
    private RemovalService<ComposedNodeDto> nodeRemovalService;
    @Inject
    @Named("ComposedNode")
    private UpdateService<RedfishComputerSystem> composedNodeUpdateService;

    @GET
    @Override
    public RedfishResourceAmazingWrapper get() {
        Context context = getCurrentContext();
        return new RedfishResourceAmazingWrapper(context, getOrThrow(() -> readerService.getResource(context)));
    }

    @DELETE
    @Override
    public Response delete() throws TimeoutException, BusinessApiException {
        nodeRemovalService.perform(getCurrentContext());
        return noContent().build();
    }

    @PATCH
    @Produces(APPLICATION_JSON)
    public Response overrideBootSource(@ComposedNodeConstraint ComposedNodePartialRepresentation representation)
        throws TimeoutException, BusinessApiException {

        composedNodeUpdateService.perform(getCurrentContext(), representation);
        return ok(get()).build();
    }

    @Path("Actions")
    public ComposedNodeActionsResource getComposedNodeActionsResource() {
        return getResource(ComposedNodeActionsResource.class);
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceBuilder()
            .addDeleteMethod()
            .addPatchMethod()
            .build();
    }
}
