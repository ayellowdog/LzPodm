/*
 * Copyright (c) 2018 Intel Corporation
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

package com.intel.podm.business.redfish.services.assembly.tasks.volume;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.client.WebClient;
import com.intel.podm.client.WebClientBuilder;
import com.intel.podm.client.WebClientRequestException;
import com.intel.podm.client.resources.redfish.VolumeResource;
//import com.intel.podm.common.enterprise.utils.logger.TimeMeasured;
import com.intel.podm.common.types.State;
import com.intel.podm.config.base.DynamicHolder;
import com.intel.podm.config.base.dto.ExternalServiceConfig;

@Component
public class WaitForVolumeTask extends NewVolumeTask {

    @Autowired
    private WebClientBuilder webClientBuilder;

    private static final Logger logger = LoggerFactory.getLogger(WaitForVolumeTask.class);
    @Autowired
//    @Config
    private DynamicHolder<ExternalServiceConfig> configHolder;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @TimeMeasured(tag = "[AssemblyTask]")
    public void run() {
        ExternalService rssService = getStorageServiceFromResourceDescriptor().getService();
        try (WebClient webClient = webClientBuilder.newInstance(rssService.getBaseUri()).retryable().build()) {
            waitForEnabledState(webClient, getNewRemoteVolumeUri());
        } catch (InterruptedException e) {
            logger.error("Remote target creation failed for Node: {}, details: {}", nodeId, e.getMessage());
        }
    }

    @Override
    public UUID getServiceUuid() {
        // synchronize with random UUID,
        // cause creating volume can be long action and does not need synchronization
        return randomUUID();
    }

    private void waitForEnabledState(WebClient webClient, URI resourceUri) throws InterruptedException {
        URI path = URI.create(resourceUri.getPath());
        while (true) {
            Optional<State> volumeState = tryReadingVolumeState(webClient, path);
            if (volumeState.isPresent()) {
                switch (volumeState.get()) {
                    case ENABLED:
                        return;
                    case ABSENT:
                        throw new RuntimeException(format("Error occurred during creation of volume on uri: %s", resourceUri));
                    default:
                        break;
                }
            }
            Thread.sleep(configHolder.get(ExternalServiceConfig.class).getCheckStatusAfterVolumeCreationDelayMillis());
        }
    }

    private Optional<State> tryReadingVolumeState(WebClient webClient, URI path) {
        try {
            VolumeResource volume = (VolumeResource) webClient.get(path);
            return ofNullable(volume.getStatus().getState());
        } catch (WebClientRequestException e) {
            return empty();
        }
    }
}
