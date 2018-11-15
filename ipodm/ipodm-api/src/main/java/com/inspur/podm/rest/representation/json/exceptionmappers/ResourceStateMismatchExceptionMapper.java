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

package com.inspur.podm.rest.representation.json.exceptionmappers;

import com.inspur.podm.business.ResourceStateMismatchException;
import com.inspur.podm.common.logger.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static com.inspur.podm.rest.error.ErrorResponseBuilder.newErrorResponseBuilder;
import static com.inspur.podm.rest.error.ErrorType.RESOURCES_STATE_MISMATCH;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@ApplicationScoped
@Provider
@Produces(APPLICATION_JSON)
public class ResourceStateMismatchExceptionMapper implements ExceptionMapper<ResourceStateMismatchException> {
    @Inject
    private Logger logger;

    @Override
    public Response toResponse(ResourceStateMismatchException exception) {
        logger.e(exception.getMessage(), exception);
        return newErrorResponseBuilder(RESOURCES_STATE_MISMATCH)
            .withMessage(format("%s exception encountered.", ResourceStateMismatchException.class.getSimpleName()))
            .withDetails(singletonList(exception.getMessage()))
            .build();
    }
}
