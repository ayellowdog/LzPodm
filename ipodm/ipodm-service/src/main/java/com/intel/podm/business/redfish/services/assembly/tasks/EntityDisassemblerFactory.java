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

package com.intel.podm.business.redfish.services.assembly.tasks;

import static com.intel.podm.common.utils.Unproxier.unproxy;
import static java.util.Optional.ofNullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.intel.podm.business.entities.redfish.ComposedNode;
import com.intel.podm.business.entities.redfish.DiscoverableEntity;
import com.intel.podm.business.entities.redfish.Endpoint;
import com.intel.podm.business.entities.redfish.Volume;
import com.intel.podm.business.entities.redfish.Zone;
import com.intel.podm.client.WebClient;
import com.intel.podm.common.enterprise.utils.beans.BeanFactory;

@Component
public class EntityDisassemblerFactory {

    private final Map<Class, Supplier<EntityDisassembler>> knownCreators = new HashMap<>();

    @Autowired
    private BeanFactory beanFactory;

    @PostConstruct
    public void init() {
        knownCreators.put(Endpoint.class, () -> beanFactory.create(EndpointDisassembler.class));
        knownCreators.put(Volume.class, () -> beanFactory.create(VolumeDisassembler.class));
        knownCreators.put(Zone.class, () -> beanFactory.create(ZoneDisassembler.class));
    }

    Builder createDisassemblerFor(DiscoverableEntity entity, ComposedNode node) {
        return new Builder(entity, node);
    }

    @SuppressWarnings("checkstyle:MethodCount")
    final class Builder {

        private EntityDisassembler disassembler;

        private Builder(DiscoverableEntity entity, ComposedNode node) {
            this.disassembler = ofNullable(knownCreators.get(unproxy(entity.getClass())))
                .orElseThrow(() -> new RuntimeException("Missing implementation, provided entity is not supported: " + entity))
                .get();

            disassembler.entity = entity;
            disassembler.node = node;
        }

        Builder use(WebClient webClient) {
            disassembler.webClient = webClient;
            return this;
        }

        Builder use(Consumer<DiscoverableEntity> entityRemover) {
            disassembler.entityRemover = entityRemover;
            return this;
        }

        void decompose() {
            disassembler.decompose();
        }
    }
}
