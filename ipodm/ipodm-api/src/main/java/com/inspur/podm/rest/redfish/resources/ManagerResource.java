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

import com.inspur.podm.business.dto.ManagerDto;
import com.inspur.podm.business.services.context.Context;
import com.inspur.podm.business.services.redfish.ReaderService;
import com.inspur.podm.rest.redfish.json.templates.RedfishResourceAmazingWrapper;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static com.inspur.podm.common.types.redfish.ResourceNames.ETHERNET_INTERFACES_RESOURCE_NAME;
import static com.inspur.podm.common.types.redfish.ResourceNames.NETWORK_PROTOCOL_RESOURCE_NAME;
import static com.inspur.podm.rest.redfish.OptionsResponseBuilder.newOptionsForResourceBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@RequestScoped
@Produces(APPLICATION_JSON)
public class ManagerResource extends BaseResource {
    @Inject
    private ReaderService<ManagerDto> readerService;

    @GET
    @Override
    public RedfishResourceAmazingWrapper get() {
        Context context = getCurrentContext();
        ManagerDto managerDto = getOrThrow(() -> readerService.getResource(context));
        return new RedfishResourceAmazingWrapper(context, managerDto);
    }

    @Path(NETWORK_PROTOCOL_RESOURCE_NAME)
    public NetworkProtocolResource getNetworkProtocol() {
        return getResource(NetworkProtocolResource.class);
    }

    @Path(ETHERNET_INTERFACES_RESOURCE_NAME)
    public EthernetInterfaceCollectionResource getEthernetInterfaces() {
        return getResource(EthernetInterfaceCollectionResource.class);
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceBuilder().build();
    }
}
