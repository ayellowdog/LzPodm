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

package com.inspur.podm.service.rest.representation.json.exceptionmappers;

import com.inspur.podm.api.business.BusinessApiException;
import com.inspur.podm.common.intel.logger.Logger;
import com.inspur.podm.service.rest.error.ExternalServiceErrorResponseBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import java.util.Optional;

import static com.inspur.podm.service.rest.error.ErrorResponseBuilder.newErrorResponseBuilder;
import static com.inspur.podm.service.rest.error.ErrorType.UNKNOWN_EXCEPTION;
import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@ApplicationScoped
@Provider
@Produces(APPLICATION_JSON)
public class BusinessApiExceptionMapper implements ExceptionMapper<BusinessApiException> {

    @Inject
    private Logger logger;

    @Inject
    private ExternalServiceErrorResponseBuilder externalServiceErrorResponseBuilder;

    @Override
    public Response toResponse(BusinessApiException exception) {

        Optional<Response> externalServiceErrorInExceptionStack = externalServiceErrorResponseBuilder.getExternalServiceErrorResponse(exception);
        if (externalServiceErrorInExceptionStack.isPresent()) {
            return externalServiceErrorInExceptionStack.get();
        }

        logger.e(format("BusinessApi exception: %s", exception.getMessage()), exception);
        return newErrorResponseBuilder(UNKNOWN_EXCEPTION).build();
    }
}
