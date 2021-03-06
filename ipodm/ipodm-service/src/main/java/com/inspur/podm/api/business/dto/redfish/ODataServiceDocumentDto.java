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

package com.inspur.podm.api.business.dto.redfish;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.inspur.podm.api.business.dto.redfish.attributes.ODataServiceDto;

@JsonPropertyOrder({"@odata.context", "values"})
public final class ODataServiceDocumentDto {
    private final List<ODataServiceDto> values;
    @JsonProperty("@odata.context")
    private final String oDataContext;

    private ODataServiceDocumentDto(Builder builder) {
        this.values = builder.values;
        this.oDataContext = builder.oDataContext;
    }

    public List<ODataServiceDto> getValues() {
        return values;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private List<ODataServiceDto> values;
        private String oDataContext;

        private Builder() {
        }

        public ODataServiceDocumentDto build() {
            return new ODataServiceDocumentDto(this);
        }

        public Builder values(List<ODataServiceDto> values, String oDataContext) {
            this.values = values;
            this.oDataContext = oDataContext;
            return this;
        }
    }
}
