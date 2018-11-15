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

import com.inspur.podm.business.BusinessApiException;
import com.inspur.podm.business.services.redfish.ActionService;
import com.inspur.podm.business.services.redfish.requests.ResetRequest;
import com.inspur.podm.rest.redfish.json.templates.actions.ResetActionJson;


import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeoutException;

import static com.inspur.podm.rest.redfish.OptionsResponseBuilder.newOptionsForResourceActionBuilder;
import static com.inspur.podm.rest.error.PodmExceptions.invalidHttpMethod;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.noContent;


@Produces(APPLICATION_JSON)
public class ResetActionResource extends BaseResource {
    @Inject
    private ActionService<ResetRequest> resetRequestActionService;

    @POST
    @Consumes(APPLICATION_JSON)
    public Response reset(ResetActionJson resetActionJson) throws TimeoutException, BusinessApiException {
        resetRequestActionService.perform(getCurrentContext(), resetActionJson);
        return noContent().build();
    }

    @Override
    public Object get() {
        return invalidHttpMethod();
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceActionBuilder()
            .addPostMethod()
            .build();
    }
}
