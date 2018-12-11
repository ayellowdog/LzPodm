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

package com.intel.podm.discovery.external.matcher;

import com.intel.podm.business.entities.redfish.ComputerSystem;
import com.intel.podm.business.entities.redfish.base.Entity;
import com.intel.podm.client.WebClientRequestException;
import com.intel.podm.client.resources.ExternalServiceResource;
import com.intel.podm.client.resources.redfish.ComputerSystemResource;

import java.util.Optional;

public interface EntityObtainerHelper<T extends ExternalServiceResource> {
    ComputerSystemResource findComputerSystemResourceFor(T resource) throws WebClientRequestException;
    Optional<? extends Entity> findEntityFor(ComputerSystem computerSystem, T resource);
    Class<? extends Entity> getEntityClass();
    Class<T> getResourceClass();
}
