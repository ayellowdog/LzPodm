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

package com.inspur.podm.service.rest.error;

import static com.inspur.podm.service.rest.error.ErrorResponseBuilder.newErrorResponseBuilder;
import static com.inspur.podm.service.rest.error.ErrorType.BAD_ACCEPT_HEADER;
import static com.inspur.podm.service.rest.error.ErrorType.INVALID_HTTP_METHOD;
import static com.inspur.podm.service.rest.error.ErrorType.NOT_FOUND;
import static com.inspur.podm.service.rest.error.ErrorType.UNKNOWN_EXCEPTION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.ext.Provider;

@ApplicationScoped
@Provider
public class DefaultErrorResponseBodyFilter implements ContainerResponseFilter {
    private static boolean hasJsonEntity(ContainerResponseContext responseContext) {
        return responseContext.getEntity() != null;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        Family family = responseContext.getStatusInfo().getFamily();
        switch (family) {
            case CLIENT_ERROR:
                handleClientError(responseContext);
                break;
            case SERVER_ERROR:
                handleServerError(responseContext);
                break;
            default:
                break;
        }
    }

    private void handleServerError(ContainerResponseContext responseContext) {
        switch ((Response.Status) responseContext.getStatusInfo()) {
            case INTERNAL_SERVER_ERROR:
                setResponseEntityIfRequired(responseContext, UNKNOWN_EXCEPTION);
                break;
            default:
                break;
        }
    }

    private void handleClientError(ContainerResponseContext responseContext) {
        switch ((Response.Status) responseContext.getStatusInfo()) {
            case NOT_FOUND:
                setResponseEntityIfRequired(responseContext, NOT_FOUND);
                break;
            case METHOD_NOT_ALLOWED:
                setResponseEntityIfRequired(responseContext, INVALID_HTTP_METHOD);
                break;
            case NOT_ACCEPTABLE:
                setResponseEntityIfRequired(responseContext, BAD_ACCEPT_HEADER);
                break;
            default:
                break;
        }
    }

    private void setResponseEntityIfRequired(ContainerResponseContext responseContext, ErrorType errorType) {
        if (!hasJsonEntity(responseContext)) {
            Response response = newErrorResponseBuilder(errorType).build();
            responseContext.setEntity(response.getEntity(), null, APPLICATION_JSON_TYPE);
        }
    }
}
