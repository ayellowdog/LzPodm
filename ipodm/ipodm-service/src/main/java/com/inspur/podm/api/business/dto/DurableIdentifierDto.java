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

package com.inspur.podm.api.business.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.intel.podm.common.types.DurableNameFormat;

@JsonPropertyOrder({"durableName", "durableNameFormat"})
public final class DurableIdentifierDto {
    private String durableName;
    private DurableNameFormat durableNameFormat;

    public String getDurableName() {
        return durableName;
    }

    public void setDurableName(String durableName) {
        this.durableName = durableName;
    }

    public DurableNameFormat getDurableNameFormat() {
        return durableNameFormat;
    }

    public void setDurableNameFormat(DurableNameFormat durableNameFormat) {
        this.durableNameFormat = durableNameFormat;
    }
}
