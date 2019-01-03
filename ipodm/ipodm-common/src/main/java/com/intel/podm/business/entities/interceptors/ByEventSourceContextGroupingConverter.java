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

package com.intel.podm.business.entities.interceptors;

import com.intel.podm.business.entities.redfish.base.Entity;
import com.intel.podm.business.entities.types.RedfishEvent;
import com.intel.podm.common.logger.Logger;
import com.intel.podm.common.types.Pair;
import com.intel.podm.common.types.events.EventType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.google.common.collect.Sets.newHashSet;
import static com.intel.podm.common.types.events.EventType.RESOURCE_ADDED;
import static com.intel.podm.common.types.events.EventType.RESOURCE_REMOVED;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@ApplicationScoped
class ByEventSourceContextGroupingConverter implements Function<List<Pair<Entity, EventType>>, List<RedfishEvent>> {
    @Inject
    private Logger logger;

    @Override
    public List<RedfishEvent> apply(List<Pair<Entity, EventType>> pairs) {
        return toRedfishEventList(groupByEventContext(pairs));
    }

    private List<RedfishEvent> toRedfishEventList(Map<URI, Set<EventType>> eventsByUri) {
        return eventsByUri.entrySet()
            .stream()
            .flatMap(entry -> entry.getValue().stream().map(value -> new RedfishEvent(entry.getKey(), value)))
            .collect(toList());
    }

    private Map<URI, Set<EventType>> groupByEventContext(List<Pair<Entity, EventType>> list) {
        return list.stream()
            .filter(filterNonNullKeys())
            .collect(
                groupingBy(
                    entityEventTypePair -> entityEventTypePair.first().getEventSourceContext(),
                    mapping(
                        Pair::second,
                        collectingAndThen(toSet(), singletonSetIfContains(RESOURCE_ADDED).compose(singletonSetIfContains(RESOURCE_REMOVED)))
                    )
                )
            );
    }

    private Predicate<Pair<Entity, EventType>> filterNonNullKeys() {
        return entityEventTypePair -> {
            Entity entity = entityEventTypePair.first();
            if (entity.getEventSourceContext() == null) {
                logger.w("EventSourceContext is not defined for entity: {}, event type: {}", entity, entityEventTypePair.second());
            }
            return entity.getEventSourceContext() != null;
        };
    }

    private Function<Set<EventType>, Set<EventType>> singletonSetIfContains(EventType eventType) {
        return eventTypes -> eventTypes.contains(eventType) ? newHashSet(eventType) : eventTypes;
    }
}
