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

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.dto.redfish.CollectionDto;
import com.inspur.podm.api.business.dto.redfish.VolumeDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.CreationService;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.inspur.podm.service.rest.redfish.json.templates.actions.CreateStorageServiceVolumeJson;
import com.inspur.podm.service.rest.redfish.json.templates.actions.constraints.VolumeCreationConstraint;
import com.intel.podm.common.types.redfish.StorageServiceVolume;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.concurrent.TimeoutException;

import static com.inspur.podm.api.business.services.context.PathParamConstants.VOLUME_ID;
import static com.inspur.podm.api.business.services.redfish.odataid.ODataIdFromContextHelper.asOdataId;
import static com.inspur.podm.service.rest.redfish.OptionsResponseBuilder.newOptionsForResourceBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.created;


@Produces(APPLICATION_JSON)
public class VolumeCollectionResource extends BaseResource {
    @Inject
    private ReaderService<VolumeDto> readerService;

    @Inject
    private CreationService<StorageServiceVolume> creationService;

    @Override
    public CollectionDto get() {
        return getOrThrow(() -> readerService.getCollection(getCurrentContext()));
    }

    @Path(VOLUME_ID)
    public VolumeResource getVolume() {
        return getResource(VolumeResource.class);
    }

    @POST
    @Consumes(APPLICATION_JSON)
    public Response createVolume(@VolumeCreationConstraint CreateStorageServiceVolumeJson representation) throws BusinessApiException, TimeoutException {
        Context storageServiceContext = getCurrentContext();

        Context createdVolumeContext = creationService.create(storageServiceContext, representation);
        return created(URI.create(asOdataId(createdVolumeContext).toString())).build();
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceBuilder().build();
    }
}
