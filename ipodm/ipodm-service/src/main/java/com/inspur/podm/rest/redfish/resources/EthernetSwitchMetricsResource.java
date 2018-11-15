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

package com.inspur.podm.rest.redfish.resources;

import static com.inspur.podm.business.services.context.SingletonContext.singletonContextOf;
import static com.inspur.podm.common.types.redfish.ResourceNames.ETHERNET_SWITCH_METRICS_RESOURCE_NAME;
import static com.inspur.podm.rest.redfish.OptionsResponseBuilder.newOptionsForResourceBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.inspur.podm.business.dto.EthernetSwitchMetricsDto;
import com.inspur.podm.business.services.context.Context;
import com.inspur.podm.business.services.redfish.ReaderService;
import com.inspur.podm.rest.redfish.json.templates.RedfishResourceAmazingWrapper;


@Produces(APPLICATION_JSON)
public class EthernetSwitchMetricsResource extends BaseResource {
    @Inject
    private ReaderService<EthernetSwitchMetricsDto> readerService;

    @GET
    @Override
    public RedfishResourceAmazingWrapper get() {
        Context context = getCurrentContext();
        EthernetSwitchMetricsDto ethernetSwitchMetricsDto = getOrThrow(() -> readerService.getResource(context));
        return new RedfishResourceAmazingWrapper(singletonContextOf(context, ETHERNET_SWITCH_METRICS_RESOURCE_NAME), ethernetSwitchMetricsDto);
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceBuilder().build();
    }
}