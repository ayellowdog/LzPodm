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

package com.inspur.podm.common.config.base.dto;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspur.podm.common.config.base.ConfigFile;

@ConfigFile(filename = "inband-service-config.json")
@Component
public class InBandServiceConfig extends BaseConfig {
    @JsonProperty("InBandServiceSupportEnabled")
    private boolean inBandServiceSupportEnabled;

    public boolean isInBandServiceSupportEnabled() {
        return inBandServiceSupportEnabled;
    }
}
