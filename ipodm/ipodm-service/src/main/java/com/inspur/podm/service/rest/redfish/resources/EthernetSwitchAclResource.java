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

import static com.inspur.podm.service.rest.redfish.OptionsResponseBuilder.newOptionsForResourceBuilder;
import static com.intel.podm.common.types.redfish.ResourceNames.RULES_RESOURCE_NAME;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.noContent;

import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.api.business.dto.EthernetSwitchAclDto;
import com.inspur.podm.api.business.services.context.Context;
import com.inspur.podm.api.business.services.redfish.ReaderService;
import com.inspur.podm.api.business.services.redfish.RemovalService;
import com.inspur.podm.service.rest.redfish.json.templates.RedfishResourceAmazingWrapper;


@Produces(APPLICATION_JSON)
@SuppressWarnings("checkstyle:ClassFanOutComplexity")
public class EthernetSwitchAclResource extends BaseResource {
    @Inject
    private ReaderService<EthernetSwitchAclDto> readerService;

    @Inject
    private RemovalService<EthernetSwitchAclDto> removalService;

    @GET
    @Override
    public RedfishResourceAmazingWrapper get() {
        Context context = getCurrentContext();
        EthernetSwitchAclDto dto = getOrThrow(() -> readerService.getResource(context));
        return new RedfishResourceAmazingWrapper(context, dto);
    }

    @DELETE
    @Override
    public Response delete() throws BusinessApiException, TimeoutException {
        removalService.perform(getCurrentContext());
        return noContent().build();
    }

    @Path(RULES_RESOURCE_NAME)
    public EthernetSwitchAclRuleCollectionResource getEthernetAclRuleCollectionResource() {
        return getResource(EthernetSwitchAclRuleCollectionResource.class);
    }

    @Path("Actions")
    public EthernetSwitchAclActionsResource getEthernetSwitchAclActionsResource() {
        return getResource(EthernetSwitchAclActionsResource.class);
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceBuilder()
            .addDeleteMethod()
            .build();
    }
}
