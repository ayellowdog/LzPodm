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

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.inspur.podm.api.business.BusinessApiException;

import java.util.concurrent.TimeoutException;

import static com.inspur.podm.service.rest.error.PodmExceptions.invalidHttpMethod;
import static com.inspur.podm.service.rest.redfish.OptionsResponseBuilder.newOptionsForResourceActionBuilder;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


@Produces(APPLICATION_JSON)
public class SwitchActionResource extends BaseResource {
    @Override
    public Object get() {
        return invalidHttpMethod();
    }

    @Path("Switch.Reset")
    public ResetActionResource reset() throws TimeoutException, BusinessApiException {
        return getResource(ResetActionResource.class);
    }

    @Override
    protected Response createOptionsResponse() {
        return newOptionsForResourceActionBuilder().build();
    }
}
