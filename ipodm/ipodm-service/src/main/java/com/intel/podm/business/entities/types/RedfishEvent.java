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

package com.intel.podm.business.entities.types;

import com.intel.podm.common.types.events.EventType;

import java.net.URI;
import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.intel.podm.common.utils.Contracts.requiresNonNull;

public final class RedfishEvent {

    private final URI uri;
    private final EventType eventType;

    public RedfishEvent(URI uri, EventType eventType) {
        requiresNonNull(uri, "uri");
        requiresNonNull(eventType, "eventType");
        this.uri = uri;
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public URI getUri() {
        return uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RedfishEvent that = (RedfishEvent) o;

        return Objects.equals(uri, that.uri)
            && Objects.equals(eventType, that.eventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, eventType);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
            .add("uri", uri)
            .add("eventType", eventType)
            .toString();
    }
}
