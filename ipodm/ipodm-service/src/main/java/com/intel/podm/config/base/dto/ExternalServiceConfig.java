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

package com.intel.podm.config.base.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intel.podm.config.base.ConfigFile;

import java.time.Duration;

import org.springframework.stereotype.Component;

import static java.time.Duration.ofHours;

@ConfigFile(filename = "external-services.json")
@Component
public class ExternalServiceConfig extends BaseConfig {

    @JsonProperty("RetainUnavailableServicesForHours")
    private long retainUnavailableServicesForHours = 720;

    @JsonProperty("CheckStatusAfterVolumeCreationDelayMillis")
    private int checkStatusAfterVolumeCreationDelayMillis = 5000;

    public int getCheckStatusAfterVolumeCreationDelayMillis() {
        return checkStatusAfterVolumeCreationDelayMillis;
    }

    public Duration getServiceRemovalDelay() {
        return ofHours(retainUnavailableServicesForHours);
    }
}