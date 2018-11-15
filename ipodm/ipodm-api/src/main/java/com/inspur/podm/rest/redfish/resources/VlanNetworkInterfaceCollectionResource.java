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

package com.inspur.podm.rest.redfish.resources;

import com.inspur.podm.business.BusinessApiException;
import com.inspur.podm.business.dto.VlanNetworkInterfaceDto;
import com.inspur.podm.business.dto.redfish.CollectionDto;
import com.inspur.podm.business.services.context.Context;
import com.inspur.podm.business.services.redfish.CreationService;
import com.inspur.podm.business.services.redfish.ReaderService;
import com.inspur.podm.common.types.redfish.RedfishVlanNetworkInterface;
import com.inspur.podm.rest.redfish.OptionsResponseBuilder;
import com.inspur.podm.rest.redfish.json.templates.actions.CreateVlanJson;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeoutException;

import static com.inspur.podm.business.services.context.ContextType.ETHERNET_SWITCH_PORT;
import static com.inspur.podm.business.services.context.PathParamConstants.ETHERNET_SWITCH_PORT_VLAN_ID;
import static com.inspur.podm.rest.redfish.OptionsResponseBuilder.newOptionsForResourceBuilder;
import static com.inspur.podm.rest.error.PodmExceptions.invalidHttpMethod;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@RequestScoped
@Produces(APPLICATION_JSON)
public class VlanNetworkInterfaceCollectionResource extends BaseResource {
    @Inject
    private ReaderService<VlanNetworkInterfaceDto> readerService;

    @Inject
    private CreationService<RedfishVlanNetworkInterface> creationService;

    @GET
    @Override
    public CollectionDto get() {
        return getOrThrow(() -> readerService.getCollection(getCurrentContext()));
    }

    @Path(ETHERNET_SWITCH_PORT_VLAN_ID)
    public VlanNetworkInterfaceResource getEthernetSwitchPortVlans() {
        return getResource(VlanNetworkInterfaceResource.class);
    }

    @POST
    @Consumes(APPLICATION_JSON)
    public Response createVlan(CreateVlanJson representation) throws TimeoutException, BusinessApiException {
        Context currentContext = getCurrentContext();

        if (!isPostEnabled(currentContext)) {
            throw invalidHttpMethod("Vlan cannot be created in specified resource");
        }

        Context createdContext = creationService.create(currentContext, representation);
        return Response.created(createdContext.asOdataId().toUri()).build();
    }

    private boolean isPostEnabled(Context currentContext) {
        return ETHERNET_SWITCH_PORT.equals(currentContext.getType());
    }

    @Override
    protected Response createOptionsResponse() {
        OptionsResponseBuilder optionsResponseBuilder = newOptionsForResourceBuilder();

        if (isPostEnabled(getCurrentContext())) {
            optionsResponseBuilder.addPostMethod();
        }

        return optionsResponseBuilder
            .build();
    }
}
