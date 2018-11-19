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

import static com.inspur.podm.api.business.services.context.PathParamConstants.ETHERNET_SWITCH_ACL_ID;
import static com.inspur.podm.service.rest.redfish.OptionsResponseBuilder.newOptionsForResourceBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.dto.EthernetSwitchAclDto;
import com.inspur.podm.api.business.dto.redfish.CollectionDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.EthernetSwitchAclCreationService;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.inspur.podm.service.rest.redfish.json.templates.actions.CreateAclJson;


@Produces(APPLICATION_JSON)
public class EthernetSwitchAclCollectionResource extends BaseResource {
    @Inject
    private ReaderService<EthernetSwitchAclDto> readerService;

    @Inject
    private EthernetSwitchAclCreationService creationService;

    @GET
    @Override
    public CollectionDto get() {
        return getOrThrow(() -> readerService.getCollection(getCurrentContext()));
    }

    @POST
    @Consumes(APPLICATION_JSON)
    public Response createAcl(CreateAclJson representation) throws BusinessApiException, TimeoutException {
        Context context = creationService.create(getCurrentContext());
        return Response.created(context.asOdataId().toUri()).build();
    }

    @Path(ETHERNET_SWITCH_ACL_ID)
    public EthernetSwitchAclResource getEthernetSwitchAcl() {
        return getResource(EthernetSwitchAclResource.class);
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceBuilder()
            .addPostMethod()
            .build();
    }
}
