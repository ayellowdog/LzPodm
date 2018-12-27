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

import static com.intel.podm.common.types.redfish.ResourceNames.VOLUMES_RESOURCE_NAME;
import static com.intel.podm.common.utils.Contracts.requiresNonNull;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.EntityOperationException;
import com.intel.podm.business.entities.dao.DiscoverableEntityDao;
import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.business.entities.redfish.StorageService;
import com.intel.podm.business.entities.redfish.Volume;
import com.intel.podm.client.WebClient;
import com.intel.podm.client.WebClientBuilder;
import com.intel.podm.client.WebClientRequestException;
import com.intel.podm.client.actions.StorageServiceVolumeCreationRequest;
import com.intel.podm.discovery.external.partial.StorageServiceVolumeObtainer;

@Component
public class VolumeActionsInvoker {
    @Autowired
    private WebClientBuilder webClientBuilder;

    private static final Logger logger = LoggerFactory.getLogger(VolumeActionsInvoker.class);

    @Autowired
    private DiscoverableEntityDao discoverableEntityDao;

    @Autowired
    private StorageServiceVolumeObtainer volumeObtainer;

    @Transactional(propagation = Propagation.MANDATORY)
    public Volume createVolume(StorageService storageService, StorageServiceVolumeCreationRequest request) throws EntityOperationException {
        ExternalService service = storageService.getService();
        requiresNonNull(service, "service", "There is no Service associated with selected Storage service");

        URI storageServiceUri = storageService.getSourceUri();
        URI volumeUri = performCreateVolumeAction(request, service, storageServiceUri);

        try {
            return volumeObtainer.discoverStorageServiceVolume(storageService, volumeUri);
        } catch (WebClientRequestException e) {
            String errorMessage = "Volume refreshing failed on selected Storage service";
            logger.warn(errorMessage + " on [ service: {}, path: {} ]", service.getBaseUri(), volumeUri);
            throw new EntityOperationException(errorMessage, e);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteVolume(Volume volume) throws EntityOperationException {
        ExternalService service = volume.getService();
        if (service == null) {
            throw new IllegalStateException("There is no ExternalService associated with selected Volume");
        }

        URI volumeUri = volume.getSourceUri();
        try (WebClient webClient = webClientBuilder.newInstance(service.getBaseUri()).retryable().build()) {
            webClient.delete(volumeUri);
        } catch (WebClientRequestException e) {
            String errorMessage = "Selected Volume could not be deleted";
            logger.warn(errorMessage + " on [ service: {}, path: {} ]", service.getBaseUri(), volumeUri);
            throw new EntityOperationException(errorMessage, e);
        }

        discoverableEntityDao.removeWithConnectedExternalLinks(volume);
    }

    private URI performCreateVolumeAction(StorageServiceVolumeCreationRequest request, ExternalService service, URI storageServiceUri)
        throws EntityOperationException {
        try (WebClient webClient = webClientBuilder.newInstance(service.getBaseUri()).retryable().build()) {
            URI volumesCollectionUri = UriBuilder.fromUri(storageServiceUri)
                .path(VOLUMES_RESOURCE_NAME)
                .build();

            return webClient.post(volumesCollectionUri, request);
        } catch (WebClientRequestException e) {
            String errorMessage = "Volume creation failed on selected switch Storage service";
            logger.warn(errorMessage + " on [ service: {}, path: {} ]", service.getBaseUri(), storageServiceUri);
            throw new EntityOperationException(errorMessage, e);
        }
    }
}
