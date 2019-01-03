/*
 * Copyright (c) 2017-2018 Intel Corporation
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

package com.intel.podm.eventing.json;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.intel.podm.common.types.events.EventType;

@JsonPropertyOrder({
    "oDataId", "odataContext", "odataType",
    "id", "name", "description", "events", "context"
})
@SuppressWarnings({"checkstyle:VisibilityModifier"})
public class EventArrayJson {
    @JsonProperty("@odata.id")
    public URI oDataId;
    @JsonProperty("@odata.context")
    public URI odataContext;
    @JsonProperty("@odata.type")
    public String odataType;
    @JsonProperty("Id")
    public String id;
    @JsonProperty("Name")
    public String name;
    @JsonProperty("Description")
    public String description;
    @JsonProperty("Events")
    public List<EventJson> events = new ArrayList<>();
    @JsonProperty("Context")
    public String context;

    @JsonPropertyOrder({
        "eventType", "messageId", "originOfCondition"
    })
    public static class EventJson {
        @JsonProperty("EventType")
        public EventType eventType;
        @JsonProperty("MessageId")
        public String messageId;
        @JsonProperty("OriginOfCondition")
        public String originOfCondition;
    }
}
