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

package com.inspur.podm.service.rest.redfish.resources;

import static com.inspur.podm.api.business.services.context.SingletonContext.singletonContextOf;
import static com.inspur.podm.service.rest.redfish.OptionsResponseBuilder.newOptionsForResourceBuilder;
import static com.intel.podm.common.types.redfish.ResourceNames.COMPUTER_SYSTEM_METRICS_RESOURCE_NAME;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.inspur.podm.api.business.dto.ComputerSystemMetricsDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.inspur.podm.service.rest.redfish.json.templates.RedfishResourceAmazingWrapper;


@Produces(APPLICATION_JSON)
public class ComputerSystemMetricsResource extends BaseResource {
    @Inject
    private ReaderService<ComputerSystemMetricsDto> readerService;

    @Override
    public RedfishResourceAmazingWrapper get() {
        Context context = getCurrentContext();
        ComputerSystemMetricsDto computerSystemMetricsDto = getOrThrow(() -> readerService.getResource(context));
        return new RedfishResourceAmazingWrapper(singletonContextOf(context, COMPUTER_SYSTEM_METRICS_RESOURCE_NAME), computerSystemMetricsDto);
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceBuilder().build();
    }
}
