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

import static com.inspur.podm.business.services.context.PathParamConstants.CHASSIS_ID;
import static com.inspur.podm.business.services.redfish.ReaderService.SERVICE_ROOT_CONTEXT;
import static com.inspur.podm.rest.redfish.OptionsResponseBuilder.newOptionsForResourceBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.inspur.podm.business.dto.ChassisDto;
import com.inspur.podm.business.dto.redfish.CollectionDto;
import com.inspur.podm.business.services.redfish.ReaderService;

@RequestScoped
@Produces(APPLICATION_JSON)
public class ChassisCollectionResource extends BaseResource {
    @Inject
    private ReaderService<ChassisDto> readerService;

    @GET
    @Override
    public CollectionDto get() {
        return getOrThrow(() -> readerService.getCollection(SERVICE_ROOT_CONTEXT));
    }

    @Path(CHASSIS_ID)
    public ChassisResource getChassis() {
        return getResource(ChassisResource.class);
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceBuilder().build();
    }
}
