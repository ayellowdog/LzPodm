/*
 * Copyright (c) 2017-2018 Intel Corporation
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

package com.intel.podm.business.redfish.services.actions;

import static java.lang.String.format;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.inspur.podm.api.business.EntityOperationException;
import com.intel.podm.client.WebClient;
import com.intel.podm.client.WebClientBuilder;
import com.intel.podm.client.WebClientRequestException;

@Component
public class RestRequestInvoker {

    @Autowired
    private WebClientBuilder webClientBuilder;

    private static final Logger logger = LoggerFactory.getLogger(RestRequestInvoker.class);

    public URI post(URI baseUri, URI requestUri, Object request) throws EntityOperationException {
        try (WebClient webClient = webClientBuilder.newInstance(baseUri).retryable().build()) {
            return webClient.post(requestUri, request);
        } catch (WebClientRequestException e) {
            String errorMessage = format("Operation failed on selected uri [ service: %s, path: %s ]", baseUri, requestUri);
            logger.warn(errorMessage);
            throw new EntityOperationException(errorMessage, e);
        }
    }
}
