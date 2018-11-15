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

import com.inspur.podm.business.EntityOperationException;
import com.inspur.podm.business.RequestValidationException;
import com.inspur.podm.business.services.context.Context;
import com.inspur.podm.business.services.redfish.AllocationService;
import com.inspur.podm.rest.redfish.json.templates.assembly.RequestedNodeJson;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static com.inspur.podm.rest.redfish.OptionsResponseBuilder.newOptionsForResourceActionBuilder;
import static com.inspur.podm.rest.error.PodmExceptions.invalidHttpMethod;
import static com.inspur.podm.rest.error.PodmExceptions.resourcesStateMismatch;
import static com.inspur.podm.rest.error.PodmExceptions.unsupportedCreationRequest;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@RequestScoped
@Produces(APPLICATION_JSON)
public class ComposedNodeCollectionActionsResource extends BaseResource {
    @Inject
    private AllocationService service;

    @Override
    public Object get() {
        throw invalidHttpMethod();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Path("Allocate")
    public Response allocate(RequestedNodeJson requestedNode) {
        try {
            Context context = service.allocate(requestedNode);
            return Response.created(context.asOdataId().toUri()).build();
        } catch (RequestValidationException e) {
            throw unsupportedCreationRequest(e.getViolations());
        } catch (EntityOperationException e) {
            throw resourcesStateMismatch("Conflict during allocation", e);
        }
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceActionBuilder().build();
    }
}
