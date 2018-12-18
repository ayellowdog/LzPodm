/*
 * Copyright (c) 2015-2018 Intel Corporation
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

import static com.intel.podm.common.types.redfish.ActionsResourceNames.ACTIONS_VOLUME_INITIALIZE;
import static com.intel.podm.common.utils.Contracts.requiresNonNull;
import static javax.ws.rs.core.UriBuilder.fromUri;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inspur.podm.api.business.EntityOperationException;
import com.inspur.podm.api.business.dto.actions.InitializeActionDto;
import com.intel.podm.business.entities.redfish.ExternalService;
import com.intel.podm.business.entities.redfish.Volume;
import com.intel.podm.common.types.actions.InitializeType;

@Component
public class VolumeInitializeActionInvoker {

    @Autowired
    private RestRequestInvoker restRequestInvoker;

    @Transactional(propagation = Propagation.MANDATORY)
    public void initialize(Volume volume, InitializeType initializeType) throws EntityOperationException {
        ExternalService service = volume.getService();
        requiresNonNull(service, "There is no ExternalService associated with selected Volume");

        URI baseUri = service.getBaseUri();
        URI initializeUri = fromUri(volume.getSourceUri()).path(ACTIONS_VOLUME_INITIALIZE).build();

        restRequestInvoker.post(baseUri, initializeUri, new InitializeActionDto(initializeType));
    }
}
