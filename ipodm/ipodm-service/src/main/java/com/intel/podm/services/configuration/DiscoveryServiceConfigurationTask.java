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

package com.intel.podm.services.configuration;

import com.intel.podm.client.WebClient;
import com.intel.podm.client.WebClientBuilder;
import com.intel.podm.common.enterprise.utils.tasks.DefaultManagedTask;
import com.intel.podm.common.logger.Logger;
import com.intel.podm.common.types.discovery.ServiceEndpoint;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static com.intel.podm.common.utils.Contracts.requiresNonNull;

@Dependent
@SuppressWarnings({"checkstyle:IllegalCatch"})
public class DiscoveryServiceConfigurationTask extends DefaultManagedTask implements Runnable {
    @Inject
    private DiscoveryServiceConfigurer configurer;

    @Inject
    private WebClientBuilder webClientBuilder;

    @Inject
    private Logger logger;

    private ServiceEndpoint serviceEndpoint;

    @Override
    public void run() {
        requiresNonNull(serviceEndpoint, "ServiceEndpoint");

        try (WebClient webClient = webClientBuilder.newInstance(serviceEndpoint.getEndpointUri()).retryable().build()) {
            configurer.configureUsingWebClient(webClient);
        } catch (Exception e) {
            logger.e("Execution of DiscoveryService configuration task failed.", e);
        }
    }

    void setServiceEndpoint(ServiceEndpoint serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
    }
}
