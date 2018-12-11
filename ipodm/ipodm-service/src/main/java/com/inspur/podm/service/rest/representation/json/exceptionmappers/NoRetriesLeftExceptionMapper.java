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

package com.inspur.podm.service.rest.representation.json.exceptionmappers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.intel.podm.common.logger.Logger;
import com.intel.podm.common.types.exceptions.NoRetriesLeftException;

import static com.inspur.podm.service.rest.error.ErrorResponseBuilder.newErrorResponseBuilder;
import static com.inspur.podm.service.rest.error.ErrorType.SERVICE_UNAVAILABLE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@ApplicationScoped
@Provider
@Produces(APPLICATION_JSON)
public class NoRetriesLeftExceptionMapper implements ExceptionMapper<NoRetriesLeftException> {
    @Inject
    private Logger logger;

    @Override
    public Response toResponse(NoRetriesLeftException exception) {
        logger.e("Application Error: " + exception.getMessage(), exception);
        return newErrorResponseBuilder(SERVICE_UNAVAILABLE).build();
    }
}
