/*
 * Copyright (c) 2015-2018 inspur Corporation
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

package com.inspur.podm.rest.redfish.json.templates;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.inspur.podm.business.services.redfish.odataid.ODataId;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"@odata.context", "@odata.id", "@odata.type", "name", "description", "Members@odata.count", "members"})
@SuppressWarnings({"checkstyle:VisibilityModifier"})
public final class CollectionJson extends BaseJson {
    public String name;
    public String description;

    public final List<ODataId> members = new ArrayList<>();

    public CollectionJson(String type) {
        super(type);
    }

    @JsonProperty("Members@odata.count")
    private int getCount() {
        return members.size();
    }
}
