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

package com.inspur.podm.rest.redfish.json.templates.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspur.podm.common.types.redfish.RedfishDrive;

@SuppressWarnings({"checkstyle:VisibilityModifier"})
public class DrivePartialRepresentation implements RedfishDrive {
    @JsonProperty("Oem")
    public Oem oem = new Oem();

    @JsonProperty("AssetTag")
    public String assetTag;

    @Override
    public String getAssetTag() {
        return assetTag;
    }

    @Override
    public Boolean getEraseOnDetach() {
        return oem.rackScaleOem.eraseOnDetach;
    }

    public static class Oem {

        @JsonProperty("inspur_RackScale")
        public RackScaleOem rackScaleOem = new RackScaleOem();

        public static class RackScaleOem {
            public Boolean eraseOnDetach;
        }
    }
}
