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

package com.inspur.podm.api.business.dto.actions.actionInfo;

import com.inspur.podm.api.business.services.redfish.odataid.ODataId;
import com.inspur.podm.common.intel.types.Protocol;

import static com.inspur.podm.common.intel.types.actions.ParameterDataType.OBJECT;
import static com.inspur.podm.common.intel.types.actions.ParameterDataType.STRING;

import java.util.Set;

public final class ParametersDtoBuilder {

    private ParametersDtoBuilder() {
    }

    public static ParameterDto asResourceParameterDto(Set<ODataId> allowableValues) {
        ParameterDto resource = new ParameterDto();
        resource.setName("Resource");
        resource.setRequired(true);
        resource.setDataType(OBJECT);
        resource.setObjectDataType("#Resource.Resource");
        resource.addAllowableValues(allowableValues);
        return resource;
    }

    public static ParameterDto asProtocolParameterDto(Set<Protocol> allowableValues) {
        ParameterDto protocol = new ParameterDto();
        protocol.setName("Protocol");
        protocol.setRequired(false);
        protocol.setDataType(STRING);
        protocol.addAllowableValues(allowableValues);
        return protocol;
    }
}
