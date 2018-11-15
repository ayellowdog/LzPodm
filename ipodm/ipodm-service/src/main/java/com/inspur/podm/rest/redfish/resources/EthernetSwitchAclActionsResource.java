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

import static com.inspur.podm.rest.error.PodmExceptions.invalidHttpMethod;
import static com.inspur.podm.rest.redfish.OptionsResponseBuilder.newOptionsForResourceActionBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.noContent;

import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.inspur.podm.business.BusinessApiException;
import com.inspur.podm.business.services.redfish.ActionService;
import com.inspur.podm.business.services.redfish.requests.BindPortActionRequest;
import com.inspur.podm.business.services.redfish.requests.UnbindPortActionRequest;
import com.inspur.podm.rest.redfish.json.templates.actions.AclPortActionJson;
import com.inspur.podm.rest.redfish.json.templates.actions.constraints.AclPortActionConstraint;


@Produces(APPLICATION_JSON)
public class EthernetSwitchAclActionsResource extends BaseResource {
    @Inject
    private ActionService<BindPortActionRequest> bindPortRequestActionService;

    @Inject
    private ActionService<UnbindPortActionRequest> unbindPortRequestActionService;

    @Override
    public Object get() {
        throw invalidHttpMethod();
    }

    @POST
    @Path("EthernetSwitchACL.Bind")
    @Consumes(APPLICATION_JSON)
    public Response bind(@AclPortActionConstraint AclPortActionJson request) throws BusinessApiException, TimeoutException {
        bindPortRequestActionService.perform(getCurrentContext(), request);
        return noContent().build();
    }

    @POST
    @Path("EthernetSwitchACL.Unbind")
    @Consumes(APPLICATION_JSON)
    public Object unbind(@AclPortActionConstraint AclPortActionJson request) throws BusinessApiException, TimeoutException {
        unbindPortRequestActionService.perform(getCurrentContext(), request);
        return noContent().build();
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceActionBuilder().build();
    }
}
