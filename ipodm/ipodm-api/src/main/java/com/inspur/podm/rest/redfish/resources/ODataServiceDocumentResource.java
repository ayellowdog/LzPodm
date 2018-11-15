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

import com.inspur.podm.business.dto.redfish.ODataServiceDocumentDto;
import com.inspur.podm.business.dto.redfish.attributes.ODataServiceDto;
import com.inspur.podm.common.types.ServiceKind;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.inspur.podm.common.types.ServiceKind.SINGLETON;
import static com.inspur.podm.common.types.redfish.ODataServices.ODATA_ROOT_SERVICES;
import static com.inspur.podm.rest.redfish.OptionsResponseBuilder.newOptionsForResourceBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@RequestScoped
@Produces(APPLICATION_JSON)
public class ODataServiceDocumentResource extends BaseResource {
    private static final String SERVICE_ROOT = "/redfish/v1/";
    private static final String SERVICE = "Service";

    @GET
    @Override
    public ODataServiceDocumentDto get() {
        return ODataServiceDocumentDto.newBuilder()
            .values(getODataRootServices())
            .build();
    }

    private List<ODataServiceDto> getODataRootServices() {
        List<ODataServiceDto> services = new ArrayList<>();
        services.add(map(SERVICE, SINGLETON, URI.create(SERVICE_ROOT)));

        ODATA_ROOT_SERVICES.forEach(service -> services.add(map(service, SINGLETON, URI.create(SERVICE_ROOT + service))));
        return services;
    }

    private ODataServiceDto map(String name, ServiceKind serviceKind, URI url) {
        return ODataServiceDto.newBuilder()
            .name(name)
            .kind(serviceKind)
            .url(url)
            .build();
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceBuilder().build();
    }
}
