/*
 * Copyright (c) 2017-2018 inspur Corporation
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

package com.inspur.podm.service.rest.redfish.json.templates.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspur.podm.api.business.services.redfish.requests.ChangeTpmStateRequest;
import com.intel.podm.common.types.InterfaceType;

public class ChangeTpmStateActionJson implements ChangeTpmStateRequest {
    @JsonProperty("DeviceEnabled")
    private Boolean deviceEnabled;

    @JsonProperty("InterfaceType")
    private InterfaceType interfaceType;

    @JsonProperty("ClearOwnership")
    private Boolean clearOwnership;

    @Override
    public Boolean getDeviceEnabled() {
        return deviceEnabled;
    }

    @Override
    public InterfaceType getInterfaceType() {
        return interfaceType;
    }

    @Override
    public Boolean getClearOwnership() {
        return clearOwnership;
    }
}
