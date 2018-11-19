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

import static com.inspur.podm.api.business.services.context.Context.contextOf;
import static com.inspur.podm.api.business.services.context.ContextType.EVENT_SERVICE;
import static com.inspur.podm.api.business.services.context.ContextType.EVENT_SUBSCRIPTION;
import static com.inspur.podm.api.business.services.redfish.ReaderService.SERVICE_ROOT_CONTEXT;
import static com.inspur.podm.common.intel.types.Id.id;
import static com.inspur.podm.service.rest.redfish.OptionsResponseBuilder.newOptionsForResourceBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.noContent;

import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.dto.EventSubscriptionDto;
import com.inspur.podm.api.business.dto.redfish.CollectionDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.CreationService;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.inspur.podm.api.business.services.redfish.RemovalService;
import com.inspur.podm.api.business.services.redfish.requests.EventSubscriptionRequest;
import com.inspur.podm.service.rest.redfish.json.templates.RedfishResourceAmazingWrapper;
import com.inspur.podm.service.rest.redfish.json.templates.actions.EventSubscriptionRequestJson;


@Produces(APPLICATION_JSON)
@SuppressWarnings("checkstyle:ClassFanOutComplexity")
public class EventSubscriptionCollectionResource extends BaseResource {
    @Inject
    private ReaderService<EventSubscriptionDto> readerService;
    @Inject
    private CreationService<EventSubscriptionRequest> creationService;
    @Inject
    private RemovalService<EventSubscriptionRequest> removalService;

    @GET
    @Override
    public CollectionDto get() {
        return getOrThrow(() -> readerService.getCollection(SERVICE_ROOT_CONTEXT));
    }

    @POST
    @Consumes(APPLICATION_JSON)
    public Response createSubscription(EventSubscriptionRequestJson payload) throws BusinessApiException, TimeoutException {
        Context context = creationService.create(null, payload);
        return Response.created(context.asOdataId().toUri()).build();
    }

    @GET
    @Path("{id}")
    public RedfishResourceAmazingWrapper getSubscription(@PathParam("id") Long id) {
        Context context = contextFromSubscriptionId(id);
        return new RedfishResourceAmazingWrapper(
            context,
            getOrThrow(() -> readerService.getResource(context))
        );
    }

    @DELETE
    @Path("{id}")
    public Response deleteSubscription(@PathParam("id") Long id) throws BusinessApiException, TimeoutException {
        removalService.perform(contextFromSubscriptionId(id));
        return noContent().build();
    }

    private Context contextFromSubscriptionId(Long id) {
        // TODO: RSASW-8103
        return contextOf(id(""), EVENT_SERVICE).child(id(id), EVENT_SUBSCRIPTION);
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceBuilder()
            .addPostMethod()
            .build();
    }
}
