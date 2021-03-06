/*
 * Copyright (c) 2018 inspur Corporation
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

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.intel.podm.config.base.ConfigFile;

@ConfigFile(filename = "discovery-service.json")
@Component
public class DiscoveryServiceConfig extends BaseConfig {
    @JsonProperty("DiscoveryServiceConfigurerIntervalSeconds")
    private long configurerIntervalSeconds = 300;

    public long getConfigurerIntervalSeconds() {
        return configurerIntervalSeconds;
    }

    @Override
    public boolean configUpdateIsAccepted(BaseConfig updatedConfig) {
        if (!(updatedConfig instanceof DiscoveryServiceConfig)) {
            return false;
        }

        DiscoveryServiceConfig newConfig = (DiscoveryServiceConfig) updatedConfig;
        return newConfig.getConfigurerIntervalSeconds() == getConfigurerIntervalSeconds();
    }
}
