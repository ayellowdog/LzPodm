/*
 * Copyright (c) 2016-2018 Intel Corporation
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

package com.intel.podm.client.actions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intel.podm.common.types.actions.LegacyRackChassisUpdateDefinition;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class LegacyRackChassisUpdateRequest {
    @JsonProperty("AssetTag")
    private String assetTag;

    @JsonProperty("Oem")
    private Oem oem;

    public LegacyRackChassisUpdateRequest(LegacyRackChassisUpdateDefinition chassisUpdateDefinition) {
        this.assetTag = chassisUpdateDefinition.getAssetTag();
        this.oem = new Oem();
        this.oem.rackScaleOem.geoTag = chassisUpdateDefinition.getGeoTag();
        this.oem.rackScaleOem.rackPuid = chassisUpdateDefinition.getRackPuid();
    }

    public static class Oem {
        @JsonProperty("Intel_RackScale")
        private RackScaleOem rackScaleOem = new RackScaleOem();

        public static class RackScaleOem {
            @JsonProperty("GeoTag")
            private String geoTag;

            @JsonProperty("RackPUID")
            private Integer rackPuid;
        }
    }
}
