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

import com.inspur.podm.business.dto.StorageServiceDto;
import com.inspur.podm.business.services.context.Context;
import com.inspur.podm.business.services.redfish.ReaderService;
import com.inspur.podm.rest.redfish.json.templates.RedfishResourceAmazingWrapper;


import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static com.inspur.podm.common.types.redfish.ResourceNames.DRIVES_RESOURCE_NAME;
import static com.inspur.podm.common.types.redfish.ResourceNames.STORAGE_POOL_RESOURCE_NAME;
import static com.inspur.podm.common.types.redfish.ResourceNames.VOLUMES_RESOURCE_NAME;
import static com.inspur.podm.rest.redfish.OptionsResponseBuilder.newOptionsForResourceBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


@Produces(APPLICATION_JSON)
public class StorageServiceResource extends BaseResource {
    @Inject
    private ReaderService<StorageServiceDto> readerService;

    @Override
    public RedfishResourceAmazingWrapper get() {
        Context context = getCurrentContext();
        StorageServiceDto storageServiceDto = getOrThrow(() -> readerService.getResource(context));
        return new RedfishResourceAmazingWrapper(context, storageServiceDto);
    }

    @Path(DRIVES_RESOURCE_NAME)
    public DriveCollectionResource getDrives() {
        return getResource(DriveCollectionResource.class);
    }

    @Path(STORAGE_POOL_RESOURCE_NAME)
    public StoragePoolCollectionResource getStoragePools() {
        return getResource(StoragePoolCollectionResource.class);
    }

    @Path(VOLUMES_RESOURCE_NAME)
    public VolumeCollectionResource getVolumes() {
        return getResource(VolumeCollectionResource.class);
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceBuilder().build();
    }
}
