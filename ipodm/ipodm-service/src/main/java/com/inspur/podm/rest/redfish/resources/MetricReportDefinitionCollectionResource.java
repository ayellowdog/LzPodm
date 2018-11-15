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

import com.inspur.podm.business.dto.MetricReportDefinitionDto;
import com.inspur.podm.business.dto.redfish.CollectionDto;
import com.inspur.podm.business.services.context.Context;
import com.inspur.podm.business.services.redfish.ReaderService;
import com.inspur.podm.rest.redfish.json.templates.RedfishResourceAmazingWrapper;


import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static com.inspur.podm.business.services.context.Context.contextOf;
import static com.inspur.podm.business.services.context.ContextType.METRIC_REPORT_DEFINITION;
import static com.inspur.podm.business.services.context.ContextType.TELEMETRY_SERVICE;
import static com.inspur.podm.business.services.context.PathParamConstants.METRIC_REPORT_DEFINITION_ID;
import static com.inspur.podm.business.services.redfish.ReaderService.SERVICE_ROOT_CONTEXT;
import static com.inspur.podm.common.types.Id.id;
import static com.inspur.podm.rest.redfish.OptionsResponseBuilder.newOptionsForResourceBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


@Produces(APPLICATION_JSON)
public class MetricReportDefinitionCollectionResource extends BaseResource {
    @Inject
    private ReaderService<MetricReportDefinitionDto> readerService;

    @GET
    @Override
    public CollectionDto get() {
        return getOrThrow(() -> readerService.getCollection(SERVICE_ROOT_CONTEXT));
    }

    @GET
    @Path("{" + METRIC_REPORT_DEFINITION_ID + "}")
    public RedfishResourceAmazingWrapper getTelemetryMetricReportDefinition(@PathParam(METRIC_REPORT_DEFINITION_ID) String id) {
        Context context = contextFromTelemetryServiceId(id);

        return new RedfishResourceAmazingWrapper(
            context,
            getOrThrow(() -> readerService.getResource(context))
        );
    }

    private Context contextFromTelemetryServiceId(String id) {
        return contextOf(id(""), TELEMETRY_SERVICE).child(id(id), METRIC_REPORT_DEFINITION);
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceBuilder().build();
    }
}

